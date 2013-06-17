//uniform vec2 m_resolution; // Screen resolution
//uniform float m_time; // time in seconds
uniform sampler2D m_ColorMap; // scene buffer
varying vec2 texCoord1;
uniform float g_Time;
// radius per unit length : w = 10.0
// radius per unit time : f = 4.0
// amplitude : A = 3.0
// unit direction : p/len
// k = 1.5
// pi = 3.14
// l = 0.5
// s = 0.8
void main(void)
{
  float k = 4.5;
  float w = 6.28/0.5;
  float A = 0.07;
  float f = 0.8 * w;
  
  vec2 tc = texCoord1.xy;
  //vec2 p = floor(tc*200.0)*0.005+0.00025;
  vec2 p = -1.0 + 2.0 * tc;
  float len = length(p);
  
  vec2 offset = w*A*(p/len)*cos(len*w-g_Time*f);
  //float p1 = (sin(len*w-g_Time*f)+1.0)/2.0;
  //float p2 = cos(len*w-g_Time*f);
  //vec2 offset =  k*(p/len)*w*A*pow(p1, k-1.0)*p2;
  
  vec2 uv = tc+offset*0.02;
  
  vec3 normal = normalize(vec3(-offset.x, 1, -offset.y));
  vec3 lightDir = normalize(vec3(0.0, 0.5, 1.0));
  float NdotL = dot(normal, lightDir);
  vec3 diffuse = vec3(0.3, 0.3, 0.3);
  vec3 lightCol = diffuse * NdotL;
  
  vec3 col = texture2D(m_ColorMap,uv).xyz;
  vec3 ambient = col * vec3(0.7, 0.7, 0.7);
  col = col * lightCol + ambient;
  
  gl_FragColor = vec4(col,1.0);
  
}