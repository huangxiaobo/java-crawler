//uniform vec2 m_resolution; // Screen resolution
//uniform float m_time; // time in seconds
uniform sampler2D m_ColorMap; // scene buffer
varying vec2 texCoord1;
uniform float g_Time;
void main(void)
{
  //vec4 color = vec4(1.0);
  //color *= texture2D(m_tex, texCoord1);
  //gl_FragColor = color;

  vec2 tc = texCoord1.xy;
  vec2 p = -1.0 + 2.0 * tc;
  float len = length(p);
  vec2 uv = tc + (p/len)*cos(len*12.0-g_Time*4.0)*0.03;
  vec3 col = texture2D(m_ColorMap,uv).xyz;
  gl_FragColor = vec4(col,1.0);  
  
}