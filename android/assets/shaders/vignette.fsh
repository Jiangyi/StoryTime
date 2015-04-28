varying vec4 v_color;
varying vec2 v_texCoord0;

uniform vec2 u_resolution;
uniform sampler2D u_sampler2D;

const float OUTER_RADIUS = .6;
const float INNER_RADIUS = .3;
const float INTENSITY = .5;

void main() {
	vec4 color = texture2D(u_sampler2D, v_texCoord0) * v_color;

	vec2 relativePosition = gl_FragCoord.xy / u_resolution - 0.5;
	float len = length(relativePosition);
	float vignette = smoothstep(OUTER_RADIUS, INNER_RADIUS, len);
	color.rgb = mix(color.rgb, color.rgb * vignette, INTENSITY);

	gl_FragColor = color;
}