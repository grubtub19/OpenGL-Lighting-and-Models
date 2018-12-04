#version 430

in vec2 tc;
out vec4 fragColor;

uniform mat4 m_matrix;
uniform mat4 v_matrix;
uniform mat4 proj_matrix;

layout (binding=0) uniform sampler2D texSample;

void main(void)
{

    vec4 color = texture(texSample, tc);
    if(color.a < 0.9) {
        discard;
    }
    fragColor = color;
}
