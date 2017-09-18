package es.upm.dit.aled.lab1;

import java.awt.Color;
import java.awt.Font;

public class EEG_GUI {

	private Graficos2D[] graficos;
	final private int X,Y;
	private float frecuencia;
	private float[] x_actual;
	private float[] y_actual;
	private float[] maxY,minY;
	private int fAnima;
	private int t=0;
	
	public EEG_GUI(float[] minY, float[] maxY, int nmuestras, float frecuencia, int fAnima) {
		graficos=new Graficos2D[nmuestras];
		y_actual=new float[nmuestras];
		x_actual=new float[nmuestras];
		for (int i=0; i < nmuestras; i++)
			graficos[i]=new Graficos2D(20*(i+1),20*(i+1));
		this.frecuencia=frecuencia;
		this.Y=200;
		this.X=1200;
		this.maxY=maxY;
		this.minY=minY;
		this.fAnima=fAnima;
		for (int i=0; i < graficos.length; i++) {
	        graficos[i].setCanvasSize(X,Y);
	        graficos[i].setFont(new Font(null,0, 12));
	        graficos[i].setXscale(0, X);
	        graficos[i].setYscale(minY[i], maxY[i]);
	        initCanvas(i,0);
	        graficos[i].show(fAnima);
	        y_actual[i]=(minY[i]+maxY[i])/2.0F;
	        x_actual[i]=0.0F;
		}
	}
	
	public Graficos2D[] getGraficos() {
		return graficos;
	}
	
	private void initCanvas(int i, int t) {
        graficos[i].setPenColor(Color.RED);
        graficos[i].line(0,(minY[i]+maxY[i])/2.0F,X,(minY[i]+maxY[i])/2.0F);
        graficos[i].text(-10, minY[i], ""+minY[i]);
        graficos[i].text(-10, maxY[i], ""+maxY[i]);
        graficos[i].text(-10, (minY[i]+maxY[i])/2.0F, "["+i+"] "+(minY[i]+maxY[i])/2.0F);
        float y=0.0f;
        if (Math.abs(minY[i]) > 0.1f)
        		y=(2*minY[i]+maxY[i])/3.0F;
        else
        		y=(minY[i]+maxY[i])/4.0F;
        graficos[i].text(-10, y, ""+t*(1000.0f/frecuencia));
        graficos[i].setPenColor(Color.BLACK);
	}
	
	public void dibujaMuestra(Muestra m) {
		boolean b=false;
		for (int i=0; i < graficos.length && i < m.numeroMuestras(); i++) {
			float y=m.muestra(i);
			if (y > maxY[i])
				y=maxY[i];
			if (y < minY[i])
				y=minY[i];
					
			graficos[i].line(x_actual[i], y_actual[i], x_actual[i]=x_actual[i]+frecuencia/100.0f, y_actual[i]=y);
			graficos[i].show();
			if (x_actual[i] > X) {
				x_actual[i]=0.0f;
				graficos[i].clear();
				if (!b)
					t++;
				b=true;
				initCanvas(i,t);
			}
			graficos[i].show(fAnima);
		}
	}
}
