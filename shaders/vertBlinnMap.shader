#version 430

layout (location=0) in vec3 vertPos;
layout (location=1) in vec2 texCoord;
layout (location=2) in vec3 vertNormal;

out vec3 modelVertPos;
out vec3 varyingVertPos;
out vec3 varyingLightDir;
out vec3 varyingNormal;
out vec3 varyingHalfVector;
out vec2 tc;

struct PositionalLight
{
    vec4 ambient;
    vec4 diffuse;
    vec4 specular;

    float constantAtt;
    float linearAtt;
    float quadAtt;

    vec3 position;
    float far_plane;
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

void main(void)
{
    modelVertPos = (m_matrix * vec4(vertPos,1.0)).xyz;
    varyingVertPos = (v_matrix * m_matrix * vec4(vertPos,1.0)).xyz;
    varyingLightDir = (v_matrix * vec4(light.position, 1.0)).xyz - varyingVertPos;
    varyingNormal = (norm_matrix * vec4(vertNormal,1.0)).xyz;
    varyingHalfVector = (varyingLightDir + (-varyingVertPos)).xyz;
    gl_Position = proj_matrix * v_matrix * m_matrix * vec4(vertPos,1.0);
	tc = texCoord;
}
