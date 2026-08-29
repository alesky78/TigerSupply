package it.spaghettisource.tigersupply.engine.path;

/**
 * this class represents a cubic polynomial semplified
 * 
 * 
 * @author Alessandro D'Ottavio
 *
 */
public class Cubic {

  float a,b,c,d;  /* a + b*x + c*x^2 +d*x^3 */

  public Cubic(float a, float b, float c, float d){
    this.a = a;
    this.b = b;
    this.c = c;
    this.d = d;
  }

  /**
   * evaluate the polynomial for in the x
   * 
   * @param x
   * @return
   */
  public float eval(float x) {
    return (((d*x) + c)*x + b)*x + a;
  }
}