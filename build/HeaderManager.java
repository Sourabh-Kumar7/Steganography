import java.io.File;

public class HeaderManager{

	private static final char DISCRIMINATOR = '^';
	private static final char FILLER = '_';
	private static final int HEADER_LENGTH = 50;
	private static final int NAME_LENGTH = 40; 
	private static final int SIZE_LENGTH = 9; 


	public static String getHeader(File f){

		String fName = f.getName();
		String fLength = String.valueOf(f.length());

		if(fName.length() > NAME_LENGTH )fName = fName.substring(0,NAME_LENGTH);

		while( fName.length() < NAME_LENGTH )fName = FILLER + fName;

		while( fLength.length() < SIZE_LENGTH )fLength = FILLER + fLength;

		return fName+DISCRIMINATOR+fLength;
	}

	public static boolean isHeaderValid(String header)throws Exception
	{

		int n = header.length();
		int discriminatorCount = 0;
		int fillerCount = 0;
		
		
		for(int i=0;i<n;i++){

			if(header.charAt(i) == DISCRIMINATOR)discriminatorCount++;
			if(header.charAt(i) == FILLER)fillerCount++;
		}

		if(discriminatorCount != 1)throw new Exception("Wrong Source File");
		if(fillerCount+getName(header).length()+1+String.valueOf(getLength(header)).length() != header.length())throw new Exception("Wrong Source File Selected");

		return true;
	}

	public static String getName(String header){


		return ((header.substring(0,(header.indexOf(DISCRIMINATOR)))).replaceAll("_"," ")).trim();
	}

	public static int getLength(String header){


		return Integer.parseInt(((header.substring((header.indexOf(DISCRIMINATOR)+1))).replaceAll("_"," ")).trim());
	}

	public static int getHeaderLength(){

		return HeaderManager.HEADER_LENGTH;
	}

	public static void main(String args[])
	{

		int[] new_val;
		int k=0;
		int[] my_arr = new int[1024];

		for(int i=0;i<my_arr.length;i++)my_arr[i] = i*100+(i/3+i/5+i/7)+23;

		String header = getHeader(new File("outFile.jpg")); 
		byte[] headerBytes = header.getBytes();


		System.out.println("\nENCODING\n\n***************************************\n\n");

		for(int i=0;i<50;i++){

			new_val = ByteManager.modify(headerBytes[i],new int[]{my_arr[k],my_arr[k+1],my_arr[k+2]});
			
			my_arr[k] = new_val[0];
			my_arr[k+1] = new_val[1];
			my_arr[k+2] = new_val[2];

			System.out.print((char)headerBytes[i]+" "+headerBytes[i]+" "+my_arr[k]+" "+my_arr[k+1]+" "+my_arr[k+2]+" ");
			System.out.println((char)ByteManager.patchUp(new int[]{my_arr[k],my_arr[k+1],my_arr[k+2]}));


			k+=3;
		}

		System.out.println("\n\n***************************************\nDECODING\n");

		k=0;

		for(int i=0;i<50;i++){

			System.out.println((char)ByteManager.patchUp(new int[]{my_arr[k],my_arr[k+1],my_arr[k+2]}));
			
			k+=3;

		}

	}


}