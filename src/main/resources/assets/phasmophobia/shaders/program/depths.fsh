#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D DepthSampler;

uniform mat4 ProjMat;
uniform vec2 InSize;
uniform vec2 OutSize;
uniform vec2 ScreenSize;
uniform float _FOV;
uniform mat4 InverseTransformMatrix;
uniform vec3 CameraPosition;
uniform vec3 Center;
uniform ivec4 ViewPort;
in vec4 vPosition;

in vec2 texCoord;

out vec4 fragColor;

vec4 CalcEyeFromWindow(in float depth)
{
    // derived from https://www.khronos.org/opengl/wiki/Compute_eye_space_from_window_space
    // ndc = Normalized Device Coordinates
    vec3 ndcPos;
    ndcPos.xy = ((2.0 * gl_FragCoord.xy) - (2.0 * ViewPort.xy)) / (ViewPort.zw) - 1;
    ndcPos.z = (2.0 * depth - gl_DepthRange.near - gl_DepthRange.far) / (gl_DepthRange.far - gl_DepthRange.near);
    vec4 clipPos = vec4(ndcPos, 1.);
    vec4 homogeneous = InverseTransformMatrix * clipPos;
    vec4 eyePos = vec4(homogeneous.xyz / homogeneous.w, homogeneous.w);
    return eyePos;
}

float near = 0.1;
float far  = 1000.0;
float LinearizeDepth(float depth)
{
    float z = depth * 2.0 - 1.0;
    return (near * far) / (far + near - z * (far - near));
}

const float exposure = 2.;
const float AOE = 15.;
void main(){
    float depth = LinearizeDepth(texture(DepthSampler, texCoord).r);
    float distance = length(vec3(1., (2.*texCoord - 1.) * vec2(ScreenSize.x/ScreenSize.y,1.) * tan(radians(_FOV / 2.))) * depth);

    vec2 uv = texCoord;
    float d = sqrt(pow((uv.x - 0.5),2.0) + pow((uv.y - 0.5),2.0));
    d = exp(-(d * AOE)) * exposure / (distance*0.15);
    fragColor = vec4(texture(DiffuseSampler,texCoord).rgb*d,1.0);
}
