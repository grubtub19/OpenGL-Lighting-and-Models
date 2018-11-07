import com.jogamp.opengl.GL4;

public class Square extends Shape {

    public Square(GL4 gl, float height, String texture) {
        genCoords(height);
        setup(gl, texture);
    }

    public void genCoords(float h) {
        addSquare(  new vec3(-h, -h, h), //front
                    new vec3( h, -h, h),
                    new vec3( h,  h, h),
                    new vec3(-h,  h, h));
        addSquare(  new vec3( h, -h, h), //right
                    new vec3( h, -h, -h),
                    new vec3( h,  h, -h),
                    new vec3( h,  h,  h));
        addSquare(  new vec3(-h, -h, -h), //left
                    new vec3(-h, -h,  h),
                    new vec3(-h,  h,  h),
                    new vec3(-h,  h, -h));
        addSquare(  new vec3( h, -h, -h), //back
                    new vec3(-h, -h, -h),
                    new vec3(-h,  h, -h),
                    new vec3( h,  h, -h));
        addSquare(  new vec3(-h,  h,  h), //top
                    new vec3( h,  h,  h),
                    new vec3( h,  h, -h),
                    new vec3(-h,  h, -h));
        addSquare(  new vec3(-h, -h, -h), //bottom
                    new vec3( h, -h, -h),
                    new vec3( h, -h,  h),
                    new vec3(-h, -h,  h));
    }
}
