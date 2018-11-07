#version 410

out vec4 color;

uniform mat4 m_matrix;
uniform mat4 v_matrix;
uniform mat4 proj_matrix;
uniform vec4 colorVec;

void main(void)
{	color = colorVec;
}
