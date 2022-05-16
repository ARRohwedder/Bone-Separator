import ij.*;
import ij.plugin.filter.PlugInFilter;
import ij.plugin.*;
import ij.process.*;
import ij.process.ImageConverter;
import java.awt.*;
import ij.plugin.PlugIn.*;



public class Bone_separator implements PlugInFilter {

	public int setup(String arg, ImagePlus imp) {
		if (IJ.versionLessThan("1.37j"))
			return DONE;
		else	
			return DOES_ALL+DOES_STACKS+SUPPORTS_MASKING;
	}

	public void run(ImageProcessor ip) {
		ImagePlus imp = IJ.getImage();

		ImagePlus imp2 = imp.duplicate();
		//imp2.show();
		int w = imp2.getWidth();
		int h = imp2.getHeight();
		ImageStack rgbStack = imp2.getStack();
		ImageStack redStack = new ImageStack(w,h);
		ImageStack greenStack = new ImageStack(w,h);
		ImageStack blueStack = new ImageStack(w,h);
		
		byte[] r,g,b;
		ColorProcessor cp;
		int n = rgbStack.getSize();
		for (int i=1; i<=n; i++) {
		 
		  r = new byte[w*h];
		  g = new byte[w*h];
		  b = new byte[w*h];
		  cp = (ColorProcessor)rgbStack.getProcessor(1);
		  cp.getRGB(r,g,b);
		  //rgbStack.deleteSlice(1);
		  redStack.addSlice(null,r);
		  greenStack.addSlice(null,g);
		  blueStack.addSlice(null,b);
		
		  }
		  
		  
		  String title = imp2.getTitle();
		
		ImageProcessor redip = redStack.getProcessor(1);
		ImageProcessor blueip = blueStack.getProcessor(1);
		ImageProcessor greenip = greenStack.getProcessor(1);//
		ImageProcessor bluework = blueip.duplicate();//blueStack.getProcessor(1)
		ImageProcessor bluework2 = blueip.duplicate();
		ImageProcessor redwork = redip.duplicate();
		ImageProcessor greenwork = greenip.duplicate();
		bluework.invert();
		for (int y = 0; y < h; y++)
		  for (int x = 0; x < w; x++)
		    bluework.putPixel(x,y, (bluework.get(x,y)+redwork.get(x,y)));
		bluework.invert();
		bluework.threshold(20);

		for (int y=0; y<h;y++)
		  for (int x=0; x<w;x++){
		    if (bluework.get(x,y)>250){
		      redwork.putPixel(x,y,redip.get(x,y));
		      greenwork.putPixel(x,y,greenip.get(x,y));
		      bluework2.putPixel(x,y,blueip.get(x,y));
		      }
		     if (bluework.get(x,y)<250){
		      redwork.putPixel(x,y,0);
		      greenwork.putPixel(x,y,0);
		      bluework2.putPixel(x,y,0);
		      }
		      }


		ImageStack rgbStack1 = new ImageStack(w,h);
		rgbStack1.addSlice(redwork);
		rgbStack1.addSlice(greenwork);
		rgbStack1.addSlice(bluework2);
		
		
		ImagePlus imp3 = new ImagePlus("Bone",rgbStack1);
		ImageConverter ic1 = new ImageConverter(imp3);
		ic1.convertRGBStackToRGB();
		imp3.getStack().getProcessor(1);
		imp3.show();
		
		ImageProcessor red2 = redip.duplicate();
		ImageProcessor green2 = greenip.duplicate();
		ImageProcessor blue2 = blueip.duplicate();

		for (int y=0; y<h;y++)
		  for (int x=0; x<w;x++){
		    if (bluework.get(x,y)<250){
		      red2.putPixel(x,y,redip.get(x,y));
		      green2.putPixel(x,y,greenip.get(x,y));
		      blue2.putPixel(x,y,blueip.get(x,y));
		      }
		     if (bluework.get(x,y)>250){
		      red2.putPixel(x,y,0);
		      green2.putPixel(x,y,0);
		      blue2.putPixel(x,y,0);
		      }
		      }
		
		ImageStack rgbStack2 = new ImageStack(w,h);
		rgbStack2.addSlice(red2);
		rgbStack2.addSlice(green2);
		rgbStack2.addSlice(blue2);
		
		
		ImagePlus imp4 = new ImagePlus("Residual",rgbStack2);
		ImageConverter ic2 = new ImageConverter(imp4);
		ic2.convertRGBStackToRGB();
		imp4.getStack().getProcessor(1);
		imp4.show();

	}

}

