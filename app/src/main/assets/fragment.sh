precision mediump float; //指定精度类型为medimp,片元着色器中使用浮点类型必须指明精度
varying vec2 texturePosition;  //接收从顶点着色器过来的纹理坐标易变变量
uniform float textureAlpha;
uniform sampler2D textureUniform;//纹理采样器，代表一副纹理，用于访问2D纹理
void main(){
   gl_FragColor = texture2D(textureUniform, texturePosition); //计算后此片元的颜色，此颜色值将送入渲染管线的后继阶段进行处理
}



