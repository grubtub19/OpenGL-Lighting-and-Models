#version 430

in vec3 modelVertPos;
in vec3 varyingVertPos;
in vec3 varyingLightDir;
in vec3 varyingNormal;
in vec3 varyingHalfVector;
in vec2 tc;

out vec4 fragColor;

struct PositionalLight
{
    vec4 ambient;
    vec4 diffuse;
    vec4 specular;
    float constantAtt;
    float linearAtt;
    float quadAtt;
    vec3 position;
};

struct Material
{
    vec4 ambient;
    vec4 diffuse;
    vec4 specular;
    float shininess;
};

uniform float far_plane;
uniform vec4 globalAmbient;
uniform PositionalLight light;
uniform Material material;
uniform mat4 norm_matrix;
uniform mat4 m_matrix;
uniform mat4 v_matrix;
uniform mat4 proj_matrix;

layout (binding=0) uniform sampler2D texSample;
layout (binding=1) uniform samplerCube depthMap;

float ShadowCalculation(float cosTheta, vec3 fragToLight)
{
    // get vector between fragment position and light position

    // use the light to fragment vector to sample from the depth map

    //float closestDepth = texture(depthMap, fragToLight).r;
    // it is currently in linear range between [0,1]. Re-transform back to original value
    //closestDepth *= far_plane;
    // now get current linear depth as the length between the fragment and light position
    float currentDepth = length(fragToLight);
    // now test for shadows
    float shadow = 0.0;
    float bias = 0.1 - 0.1 * (cosTheta / 2.0);
    float samples = 4.0;
    float offset = 0.01;
    //vec2 texelSize = 1.0 / textureSize(depthMap, 0);
    for(float x = -offset; x < offset; x += offset / (samples * 0.5)) {
        for(float y = -offset; y < offset; y += offset / (samples * 0.5)) {
            for(float z = -offset; z < offset; z += offset / (samples * 0.5)) {
                float closestDepth = texture(depthMap, fragToLight + vec3(x, y, z)).r;
                closestDepth *= far_plane;
                if(currentDepth - bias > closestDepth) {
                    shadow += 1.0;
                }
            }
        }
    }

    //shadow = currentDepth -  bias > closestDepth ? 1.0 : 0.0;
    shadow /= (samples * samples * samples);
    return shadow;
}

void main(void)
{
    vec4 texColor;
    texColor = texture(texSample, tc);
    if(texColor.a < 0.0) {
        discard;
    }
    vec3 L = normalize(varyingLightDir);
    vec3 N = normalize(varyingNormal);
    vec3 V = normalize(-varyingVertPos);
    vec3 R = normalize(reflect(-L, N));

    float cosTheta = dot(L,N);
    float cosPhi = dot(normalize(varyingHalfVector),R);
    vec3 fragToLight = modelVertPos - light.position;
    float distance = length(fragToLight);

    float att = 1 / (light.constantAtt + light.linearAtt * distance + light.quadAtt * distance * distance);
    vec3 ambient = ((globalAmbient * material.ambient) + (light.ambient * material.ambient)).xyz;
    vec3 diffuse = light.diffuse.xyz * material.diffuse.xyz * max(cosTheta,0.0);
    vec3 specular = light.specular.xyz * material.specular.xyz * pow(max(cosPhi,0.0), material.shininess*3);
    float shadow = ShadowCalculation(cosTheta, fragToLight);
    fragColor = texColor * vec4((ambient + (1.0 - shadow) * (diffuse + specular) * clamp(att, 0.0, 1.0)), texColor.a);

    //fragColor = texColor * vec4((ambient + (diffuse + specular)), 1.0);
}
