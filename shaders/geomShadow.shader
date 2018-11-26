#version 430

layout (triangles) in;
layout (triangle_strip, max_vertices=18) out;

uniform mat4 viewMatrices[6];
uniform int lightNum;
out vec4 FragPos; // FragPos from GS (output per emitvertex)

void main()
{
    for(int face = 0; face < 6; ++face)
    {
        //gl_Layer = face + (6 * lightNum); // built-in variable that specifies to which face we render.
        gl_Layer = face; // built-in variable that specifies to which face we render.
        for(int i = 0; i < 3; ++i) // for each triangle's vertices
        {
            FragPos = gl_in[i].gl_Position;
            gl_Position = viewMatrices[face] * FragPos;
            EmitVertex();
        }
        EndPrimitive();
    }
}