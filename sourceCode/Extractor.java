import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.FileOutputStream;

public class Extractor{

	File inputFile;

	public Extractor(File inputFile)
	{
		this.inputFile = inputFile;
		ByteManager.setFlag(0);
	}

	public void extract()throws Exception
	{

		BufferedImage inputImage = ImageIO.read(inputFile);

		System.out.println("Verifying source file");

		int width = inputImage.getWidth();
		int height = inputImage.getHeight();

		boolean done = false;
		int n = HeaderManager.getHeaderLength();
		int k = 0,x = 0,y = 0;

		WritableRaster raster = inputImage.getRaster();

		byte[] headerBuffer = new byte[ HeaderManager.getHeaderLength() ];

		for(x=0;x<width;x++){

			for(y=0;y<height;y++){

				if(k == n){

					k = 0;
					done = true;
					System.out.println("HeaderBreak at "+x+" "+y);
					break;
				}

				headerBuffer[k++] = (byte)ByteManager.patchUp(new int[]{raster.getSample(x,y,0),raster.getSample(x,y,1),raster.getSample(x,y,2)});

				// System.out.print(raster.getSample(x,y,0)+" "+raster.getSample(x,y,1)+" "+raster.getSample(x,y,2)+" ");
				// System.out.println((char)headerBuffer[k-1]);
			}
			if(done)break;
		}

		done = false;

		String header = new String(headerBuffer);
		System.out.println(header);
		if(HeaderManager.isHeaderValid(header))System.out.println("Source fIle is verified");

		String outFileName = HeaderManager.getName(header);
		System.out.println("Embedded file "+outFileName+" found ");

		File outFile = new File(outFileName);
		FileOutputStream fout = new FileOutputStream("images/restoredImage.png");

		int fileLengthRemaining = HeaderManager.getLength(header);
		System.out.println("Data hidden :: "+fileLengthRemaining);

		byte data;

		for(int i=x;i<width;i++){

			for(int j=y;j<height;j++){

				if(fileLengthRemaining == 0 ){

					System.out.println("File reading ends at "+i+" "+j);
					done = true;
					break;
				}

				data = (byte)ByteManager.patchUp(new int[]{raster.getSample(i,j,0),raster.getSample(i,j,1),raster.getSample(i,j,2)});				
				fileLengthRemaining--;
				fout.write(data);

			}

			if(done)break;
		}

		fout.close();

		System.out.println("Done with Extracting");
		
	}

	public static void main(String args[]){

		try{

			File inputFile = new File(args[0]);
			new Extractor(inputFile).extract();
		}

		catch(ArrayIndexOutOfBoundsException ex){

			System.out.println("Usage java Extractor <inputFile>");
		}

		catch(Exception ex){

			System.out.println(ex.getMessage());
		}
	}

}