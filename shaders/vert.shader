#version 430

layout (location=0) in vec3 pos;
layout (location=1) in vec2 texCoord;
out vec2 tc;

uniform mat4 m_matrix;
uniform mat4 v_matrix;
uniform mat4 proj_matrix;
layout (binding=0) uniform sampler2D samp;

void main(void)
{	gl_Position = proj_matrix * v_matrix * m_matrix * vec4(pos,1.0);
	tc = texCoord;
} 
