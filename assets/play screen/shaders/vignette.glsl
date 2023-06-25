#version 330 core
#ifdef GL_ES
precision mediump float;
#endif

// Passed in values from JAVA
uniform vec2 u_resolution;
uniform sampler2D u_texture;

varying vec4 v_color;
varying vec2 v_texCoords;

const float outerRadius = 0.65, innerRadius = 0.4, intensity = 0.9;

void main(){
    vec4 color = texture2D(u_texture, v_texCoords) * v_color;
    vec2 relativePosition = gl_FragCoord.xy / u_resolution - 0.5;
    relativePosition.x *= u_resolution.x / u_resolution.y;
    float len = length(relativePosition);
    float vignette = smoothstep(outerRadius, innerRadius, len);
    color.rbg = mix(color.rgb, color_rgb * vignette, intensity);
    gl_FragCoord = color;
}