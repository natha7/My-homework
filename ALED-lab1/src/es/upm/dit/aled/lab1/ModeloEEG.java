package es.upm.dit.aled.lab1;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.PrintStream;

public class ModeloEEG {

	private List<Muestra> muestras=new ArrayList<Muestra>();
	private EEG_GUI gui;
	
	public ModeloEEG(String backupFile) {
		try {
			leeFichero(backupFile);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}
	
	public ModeloEEG(Muestra[] muestras) {
		// TODO
		for(int i=0; i< muestras.length; i++){
			this.muestras.add(muestras[i]);
		}
	}
	
	public ModeloEEG() {
	}

    protected void escribeFichero(String fileName,int nchan, float fs_Hz, float scale_fac_uVolts_per_count) throws IOException {
    		// TODO
    	File f=new File(fileName);
    	FileOutputStream fos=new FileOutputStream(f);
    	PrintStream ps=new PrintStream(fos);
    	ModeloEEG e = new ModeloEEG();
    	ps.println(e.sintetizaDatos(nchan, fs_Hz, scale_fac_uVolts_per_count));
    	ps.close();
    	fos.close();
    }

    protected void leeFichero(String fileName) throws IOException {
    		File f=new File(fileName);
    		FileInputStream fis=new FileInputStream(f);
    		DataInput fid=new DataInputStream(fis);
	    	String line;
	    	while ((line=fid.readLine()) != null) {
	    		if (line.startsWith("%")) continue;
	    		String[] columnas=line.split(",");
	    		float[] muestrasD = new float[columnas.length-1];
	    		for (int i=1; i < columnas.length; i++)
	    			muestrasD[i-1]=Float.parseFloat(columnas[i]);
	    		nuevaMuestra(new Muestra(muestrasD));
	    	}
	    fis.close();
    }
    
    public void representaDatos() {
    		if (muestras.size() == 0) return;
    		int nmuestras=muestras.get(0).numeroMuestras();
    		float[] minY=new float[nmuestras];
    		float[] maxY=new float[nmuestras];
    		for (int i=0; i < nmuestras; i++) {
    			minY[i]=muestras.get(0).muestra(i);
    			maxY[i]=muestras.get(0).muestra(i);
    			int j=0;
	    		for (Muestra m : muestras) {
	    			float y=m.muestra(i);
	    			if (y > maxY[i])
	    				maxY[i]=y;
	    			if (y < minY[i])
	    				minY[i]=y;
	    			j++;
	    		}
    		}
    		initGrafico(minY,maxY,nmuestras);
    		for (Muestra m : muestras)
    			gui.dibujaMuestra(m);
    }
    
    protected void initGrafico(float[] minY,float[] maxY, int nmuestras) {
    		gui=new EEG_GUI(minY,maxY,nmuestras,250.0F,0);
    }
    
    public void nuevaMuestra(Muestra muestra) {
    		muestras.add(muestra);
    		if (gui != null)
    			gui.dibujaMuestra(muestra);
    }
    
    public Muestra[] getMuestras() {
    		Muestra[] am=new Muestra[muestras.size()];
    		return muestras.toArray(am);
    }
    
    public ModeloEEG filtraModelo(FiltroEEG filtro) {
    		// TODO
    	ModeloEEG modeloFiltro = filtro.aplicaFiltro(this);
    		return null;
    }
    
    private Random random=new Random();
    private final float sine_freq_Hz = 10.0f;
    
    private Muestra sintetizaDatos(int nchan, float fs_Hz, float scale_fac_uVolts_per_count) {
    	  double val_uV;
    	  double[] sine_phase_rad=new double[nchan];
    	  float[] curDataPacket_values=new float[nchan];
    	  for (int ichan=0; ichan < nchan; ichan++) {
    	      val_uV = random.nextGaussian()*Math.sqrt(fs_Hz/2.0f); // ensures that it has amplitude of one unit per sqrt(Hz) of signal bandwidth
    	      if (ichan==0) val_uV*= 10;  //scale one channel higher

    	      if (ichan==1) {
    	        //add sine wave at 10 Hz at 10 uVrms
    	        sine_phase_rad[ichan] += 2.0f*Math.PI * sine_freq_Hz / fs_Hz;
    	        if (sine_phase_rad[ichan] > 2.0f*Math.PI) sine_phase_rad[ichan] -= 2.0f*Math.PI;
    	        val_uV += 10.0f * Math.sqrt(2.0)*Math.sin(sine_phase_rad[ichan]);
    	      } else if (ichan==2) {
    	        //50 Hz interference at 50 uVrms
    	        sine_phase_rad[ichan] += 2.0f*Math.PI * 50.0f / fs_Hz;  //60 Hz
    	        if (sine_phase_rad[ichan] > 2.0f*Math.PI) sine_phase_rad[ichan] -= 2.0f*Math.PI;
    	        val_uV += 50.0f * Math.sqrt(2.0)*Math.sin(sine_phase_rad[ichan]);    //20 uVrms
    	      } else if (ichan==3) {
    	        //60 Hz interference at 50 uVrms
    	        sine_phase_rad[ichan] += 2.0f*Math.PI * 60.0f / fs_Hz;  //50 Hz
    	        if (sine_phase_rad[ichan] > 2.0f*Math.PI) sine_phase_rad[ichan] -= 2.0f*Math.PI;
    	        val_uV += 50.0f * Math.sqrt(2.0)*Math.sin(sine_phase_rad[ichan]);  //20 uVrms
    	      } 
    	      curDataPacket_values[ichan] = (int) (0.5f+ val_uV / scale_fac_uVolts_per_count); //convert to counts, the 0.5 is to ensure rounding
    	  }
	  try {
		Thread.sleep(Math.round(1000/fs_Hz));
	  } catch (InterruptedException ie) {}
    	  return new Muestra(curDataPacket_values);
    	}

    public static void main(String[] args) {
    	try{
    		ModeloEEG mod = new ModeloEEG();
    		mod.escribeFichero("file", 300, 60, 1.0f);
    	} catch (IOException ie){
    		ie.printStackTrace();
    	}
    }
  }
