package es.upm.dit.aled.lab1;

public class Muestra {

	private float[] muestra;
	public Muestra(float[] muestraEEG) {
		muestra=muestraEEG;
	}
	
	public float muestra(int numMuestra) {
		return muestra[numMuestra];
	}
	
	public int numeroMuestras() {
		return muestra.length;
	}
}
