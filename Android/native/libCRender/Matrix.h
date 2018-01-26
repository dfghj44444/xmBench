
#define OUT

namespace Matrix
{
    //矩阵乘法
    void multiplyMM(OUT float result[16], int resultOffset,
                    float lhs[16], int lhsOffset, float rhs[16], int rhsOffset);



    /**
     * Defines a projection matrix in terms of six clip planes.
     *
     * @param m the float array that holds the output perspective matrix
     * @param offset the offset into float array m where the perspective
     *        matrix data is written
     * @param left
     * @param right
     * @param bottom
     * @param top
     * @param near
     * @param far
     */
    void frustumM(float* m, int offset,
                  float left, float right, float bottom, float top,
                  float near, float far) ;

    /**
     * Defines a projection matrix in terms of a field of view angle, an
     * aspect ratio, and z clip planes.
     *
     * @param m the float array that holds the perspective matrix
     * @param offset the offset into float array m where the perspective
     *        matrix data is written
     * @param fovy field of view in y direction, in degrees
     * @param aspect width to height aspect ratio of the viewport
     * @param zNear
     * @param zFar
     */
    void perspectiveM(float* m, int offset,
                      float fovy, float aspect, float zNear, float zFar) ;

    /**
     * Computes the length of a vector.
     *
     * @param x x coordinate of a vector
     * @param y y coordinate of a vector
     * @param z z coordinate of a vector
     * @return the length of a vector
     */
    float length(float x, float y, float z) ;

    /**
     * Sets matrix m to the identity matrix.
     *
     * @param sm returns the result
     * @param smOffset index into sm where the result matrix starts
     */
    void setIdentityM(float* sm, int smOffset) ;

    /**
     * Scales matrix m by x, y, and z, putting the result in sm.
     * <p>
     * m and sm must not overlap.
     *
     * @param sm returns the result
     * @param smOffset index into sm where the result matrix starts
     * @param m source matrix
     * @param mOffset index into m where the source matrix starts
     * @param x scale factor x
     * @param y scale factor y
     * @param z scale factor z
     */
    void scaleM(float* sm, int smOffset,
                float* m, int mOffset,
                float x, float y, float z);

    /**
     * Scales matrix m in place by sx, sy, and sz.
     *
     * @param m matrix to scale
     * @param mOffset index into m where the matrix starts
     * @param x scale factor x
     * @param y scale factor y
     * @param z scale factor z
     */
    void scaleM(float* m, int mOffset,
                float x, float y, float z) ;

    /**
     * Translates matrix m by x, y, and z, putting the result in tm.
     * <p>
     * m and tm must not overlap.
     *
     * @param tm returns the result
     * @param tmOffset index into sm where the result matrix starts
     * @param m source matrix
     * @param mOffset index into m where the source matrix starts
     * @param x translation factor x
     * @param y translation factor y
     * @param z translation factor z
     */
    static void translateM(float* tm, int tmOffset,
                           float* m, int mOffset,
                           float x, float y, float z) ;

    /**
     * Translates matrix m by x, y, and z in place.
     *
     * @param m matrix
     * @param mOffset index into m where the matrix starts
     * @param x translation factor x
     * @param y translation factor y
     * @param z translation factor z
     */
    void translateM(
            float* m, int mOffset,
            float x, float y, float z) ;

    /**
     * Rotates matrix m by angle a (in degrees) around the axis (x, y, z).
     * <p>
     * m and rm must not overlap.
     *
     * @param rm returns the result
     * @param rmOffset index into rm where the result matrix starts
     * @param m source matrix
     * @param mOffset index into m where the source matrix starts
     * @param a angle to rotate in degrees
     * @param x X axis component
     * @param y Y axis component
     * @param z Z axis component
     */
    void rotateM(float* rm, int rmOffset,
                 float* m, int mOffset,
                 float a, float x, float y, float z) ;

    /**
     * Rotates matrix m in place by angle a (in degrees)
     * around the axis (x, y, z).
     *
     * @param m source matrix
     * @param mOffset index into m where the matrix starts
     * @param a angle to rotate in degrees
     * @param x X axis component
     * @param y Y axis component
     * @param z Z axis component
     */
    void rotateM(float m[], int mOffset, float a, float x, float y, float z);

    /**
     * Creates a matrix for rotation by angle a (in degrees)
     * around the axis (x, y, z).
     * <p>
     * An optimized path will be used for rotation about a major axis
     * (e.g. x=1.0f y=0.0f z=0.0f).
     *
     * @param rm returns the result
     * @param rmOffset index into rm where the result matrix starts
     * @param a angle to rotate in degrees
     * @param x X axis component
     * @param y Y axis component
     * @param z Z axis component
     */
    void setRotateM(float rm[32], int rmOffset,
                    float a, float x, float y, float z) ;

    /**
     * Converts Euler angles to a rotation matrix.
     *
     * @param rm returns the result
     * @param rmOffset index into rm where the result matrix starts
     * @param x angle of rotation, in degrees
     * @param y angle of rotation, in degrees
     * @param z angle of rotation, in degrees
     */
    void setRotateEulerM(float rm[], int rmOffset,
                         float x, float y, float z) ;

    /**
     * Defines a viewing transformation in terms of an eye point, a center of
     * view, and an up vector.
     *
     * @param rm returns the result
     * @param rmOffset index into rm where the result matrix starts
     * @param eyeX eye point X
     * @param eyeY eye point Y
     * @param eyeZ eye point Z
     * @param centerX center of view X
     * @param centerY center of view Y
     * @param centerZ center of view Z
     * @param upX up vector X
     * @param upY up vector Y
     * @param upZ up vector Z
     */
    void setLookAtM(float rm[], int rmOffset,
                    float eyeX, float eyeY, float eyeZ,
                    float centerX, float centerY, float centerZ, float upX, float upY,
                    float upZ) ;
}
