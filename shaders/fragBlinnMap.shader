#version 430

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
    vec3 position;
};

struct Material
{
    vec4 ambient;
    vec4 diffuse;
    vec4 specular;
    float shininess;
};

uniform vec4 globalAmbient;
uniform PositionalLight light;
uniform Material material;
uniform mat4 norm_matrix;
uniform mat4 m_matrix;
uniform mat4 v_matrix;
uniform mat4 proj_matrix;

layout (binding=0) uniform sampler2D texSample;
layout (binding=1) uniform samplerCube depthMap;

/*float ShadowCalculation(vec3 fragPos)
{
    vec3 fragToLight = fragPos - light.position;
    float closestDepth = texture(depthMap, fragToLight).r;
    closestDepth *= 40.0;
    float currentDepth = length(fragToLight);
    float bias = 0.05;
    float shadow = currentDepth -  bias > closestDepth ? 1.0 : 0.0;
    return shadow;
}*/

void main(void)
{

    vec4 texColor = texture(texSample, tc);
    /*
    vec3 fragToLight = varyingVertPos - light.position;
    float distanceToLight = length(framToLight);
    float currentDistanceToLight = distanceToLight / 40.0;
    currentDistanceToLight = clamp(currentDistanceToLight, 0 , 1);
    fragToLight = normalize(fragToLight);
    float referenceDistanceToLight = texture(depthMap, fragToLight).r;
    float shadowFactor = float(referenceDistanceToLight > currentDistanceToLight);
*/

    vec3 fragToLight = varyingVertPos - light.position;
    /*
    float closestDepth = texture(depthMap, fragToLight).r;
    closestDepth *= 40.0;
    float currentDepth = length(fragToLight);
    float bias = 0.05;
    float res = currentDepth - bias;
    float shadow;
    if(res > 0.0) {
        shadow = 1.0;
    } else {
        shadow = 0.0;
    }*/
    vec3 L = normalize(varyingLightDir);
    vec3 N = normalize(varyingNormal);
    vec3 V = normalize(-varyingVertPos);
    vec3 R = normalize(reflect(-L, N));

    float cosTheta = dot(L,N);
    float cosPhi = dot(normalize(varyingHalfVector),R);

    vec3 ambient = ((globalAmbient * material.ambient) + (light.ambient * material.ambient)).xyz;
    vec3 diffuse = light.diffuse.xyz * material.diffuse.xyz * max(cosTheta,0.0);
    vec3 specular = light.specular.xyz * material.specular.xyz * pow(max(cosPhi,0.0), material.shininess*3);
    //fragColor = texColor * vec4((ambient + (1.0 - shadow) * (diffuse + specular)), 1.0);
    //fragColor = texColor * vec4((ambient + diffuse + specular), 1.0);
    //vec3 fragToLight = varyingVertPos - light.position;
    fragColor = vec4(texture(depthMap, fragToLight).r, 1.0, 1.0, 1.0);
    //fragColor = vec4(1.0, 0.0, 0.0, 1.0);
}
