#version 300 es
precision mediump float;
layout(location = 0) in vec4 a_position;
layout(location = 1) in vec2 a_texCoord;
layout(location = 2) in vec3 a_normal;
uniform mat4 u_MVPMatrix;
uniform mat4 u_ModelMatrix;
uniform vec3 lightPos;
uniform vec3 lightColor;
uniform vec3 viewPos;
out vec2 v_texCoord;
out vec3 ambient;
out vec3 diffuse;
out vec3 specular;
void main()
{
    gl_Position = u_MVPMatrix * a_position;
    vec3 fragPos = vec3(u_ModelMatrix * a_position);

    // Ambient 环境光
    float ambientStrength = 0.1;
    ambient = ambientStrength * lightColor;

    // Diffuse 散射光
    //散射光最终强度 = 材质反射系数 × 散射光强度 × max(cos(入射角)，0)
    float diffuseStrength = 0.5;
    vec3 unitNormal = normalize(vec3(u_ModelMatrix * vec4(a_normal, 1.0)));
    vec3 lightDir = normalize(lightPos - fragPos);
    float diff = max(dot(unitNormal, lightDir), 0.0);
    diffuse = diffuseStrength  * lightColor * diff;

    // Specular 镜面光
    // 镜面光最终强度 = 材质镜面亮度因子 × 镜面光强度 × max(cos(反射光向量与视线方向向量夹角)，0)
    float specularStrength = 0.9;
    vec3 viewDir = normalize(viewPos - fragPos);
    vec3 reflectDir = reflect(-lightDir, unitNormal);
    float spec = pow(max(dot(unitNormal, reflectDir), 0.0), 16.0);
    specular = specularStrength * spec * lightColor;
    v_texCoord = a_texCoord;
}

