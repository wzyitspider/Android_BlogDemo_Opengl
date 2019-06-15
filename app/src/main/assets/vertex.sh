uniform mat4 mvpMatrix; //总变换矩阵
attribute vec3 vertexPosition; //顶点位置
attribute vec2 textureCoor;//纹理位置
varying vec2 texturePosition; //用于传递给片元着色器的变量
void main(){   //方法
   gl_Position = mvpMatrix * vec4(vertexPosition, 1);  //根据总变换矩阵计算此次绘制此顶点位置,gl_Position是经过变换矩阵变换,投影后的顶点的最终位置，传递到渲染管线的后继阶段进行处理
   texturePosition = textureCoor;  //将接收的颜色传递给片元着色器
}
