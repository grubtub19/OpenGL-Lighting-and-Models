#version 430

in vec4 FragPos;

out vec4 fragColor;

uniform vec3 lightPos;
uniform float far_plane;

void main()
{
    // get distance between fragment and light source
    //float lightDistance = distance(FragPos.xyz, lightPos);

    // map to [0;1] range by dividing by far_plane
    //lightDistance = lightDistance / far_plane;

    // write this as modified depth
    //gl_FragDepth = lightDistance;
    //gl_FragDepth = 0.0;
    //fragColor = vec4(0.0,1.0,0.0,1.0);

    //gl_FragDepth = fragPos;
    //fragColor = fragPos;
    fragColor = vec4(1.0,1.0,1.0,1.0);
}
