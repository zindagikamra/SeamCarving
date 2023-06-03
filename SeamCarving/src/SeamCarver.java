import java.awt.Color;
public class SeamCarver
{
	private SmC_Picture origPic;
	private SmC_Picture picture;
	private double[][] energy;
	private boolean lastSeamRemovalWasVertical = true;
	
	public SeamCarver(SmC_Picture pictureP)
	{
		if(pictureP == null)
		{
			throw new java.lang.NullPointerException();  
		}
		origPic = pictureP;
		picture = new SmC_Picture(pictureP);
		energy = new double[height()][width()];
		for(int i = 0; i < height(); i++)
		{
			for(int j = 0; j < width(); j++)
			{
				energy[i][j] = energy(j,i);
				
			}
		}
	}

	public SmC_Picture picture()
	{
		return picture;
	}

	public int width()
	{
		return picture.width();
	}

	public int height()
	{
		return picture.height();
	}

	public double energy(int x, int y)
	{
		if(x < 0 || x >= width() || y < 0 || y >= height())
		{
			throw new java.lang.IndexOutOfBoundsException();
		}
		if(x == width()-1 || y == height()-1 || x == 0 || y == 0) 
		{
			return 1000;
		}
		return Math.sqrt(energyX(x,y)+energyY(x,y));
	}
	
	private double energyX(int x, int y)
	{
		double diffR = Math.pow(   picture.get(x+1, y).getRed() - picture.get(x-1, y).getRed()  ,2);
		double diffG = Math.pow(   picture.get(x+1, y).getGreen() - picture.get(x-1, y).getGreen()   , 2);
		double diffB = Math.pow(   picture.get(x+1, y).getBlue() - picture.get(x-1, y).getBlue()   , 2);
		return diffR + diffG + diffB;
	}
	
	private double energyY(int x, int y)
	{
		double diffR = Math.pow(   picture.get(x, y+1).getRed() - picture.get(x, y-1).getRed()  ,2);
		double diffG = Math.pow(   picture.get(x, y+1).getGreen() - picture.get(x, y-1).getGreen()   , 2);
		double diffB = Math.pow(   picture.get(x, y+1).getBlue() - picture.get(x, y-1).getBlue()   , 2);
		return diffR + diffG + diffB;
	}

	public int[] findHorizontalSeam()
	{
		double[][] origArray = new double[energy.length][energy[0].length];
		
		for(int i = 0; i < energy.length; i++)
		{
			for(int j = 0; j < energy[0].length; j++)
			{
				origArray[i][j] = energy[i][j]; 
			}
		}
		
		//boolean[][] marked = new boolean[energy.length][energy[0].length];
		double[][] newArray = new double[energy[0].length][energy.length];
		for(int i = 0; i < energy.length; i++)
		{
			for(int j = 0; j < energy[0].length; j++)
			{
				newArray[j][i] = energy[i][j];
			}
		}
		energy = newArray;
		int[] shortestPath = findVerticalSeam();
		energy = origArray;
		return shortestPath;
	}

	public int[] findVerticalSeam()
	{
		//Keeps distance from top row according to energy
		double[][] distTo = new double[energy.length][energy[0].length];
		//Keeps parent pixel according to x value
		double[][] prev = new double[energy.length][energy[0].length];
		
		for(int i = 0; i < energy.length; i++)
		{
			for(int j = 0; j < energy[0].length; j++)
			{
				prev[i][j] = -1;
				distTo[i][j] = Double.POSITIVE_INFINITY;
				if(i == 0)
				{
					distTo[0][j] = 0.0;
				}
			}
		}
		
		for(int y = 0; y < energy.length; y++)
		{
			for(int x = 0; x < energy[0].length; x++)
			{
				if(y != energy.length-1)
				{
					if(distTo[y][x]+ energy[y+1][x] < distTo[y+1][x])
					{
						distTo[y+1][x] = distTo[y][x]+ energy[y+1][x];
						prev[y+1][x] = x;
					}
					if(x != 0)
					{
						//System.out.println("I work");
						if(distTo[y][x]+ energy[y+1][x-1] < distTo[y+1][x-1])
						{
							distTo[y+1][x-1] = distTo[y][x]+ energy[y+1][x-1];
							prev[y+1][x-1] = x;
						}
					}
					//System.out.println("Im here");
					if(x != energy[0].length-1)
					{
						//System.out.println(width());
						if(distTo[y][x]+ energy[y+1][x+1] < distTo[y+1][x+1])
						{
							distTo[y+1][x+1] = distTo[y][x]+ energy[y+1][x+1];
							prev[y+1][x+1] = x;
						}
						//System.out.println("Here too");
					}
					//System.out.println("Here too");
					//System.out.println();
				}
			}
		}
		int minIndex = 0;
		for(int i = 1; i < energy[0].length; i++)
		{
			if(distTo[energy.length-1][i] < distTo[energy.length-1][minIndex])
			{
				minIndex = i;
			}
		}
		
		int[] path = new int[energy.length];
		for(int i = energy.length-1; i >= 0; i--)
		{
			path[i] = minIndex;
			minIndex = (int) prev[i][minIndex];
		}
		
		return path;
	}
	
	

	public void removeHorizontalSeam(int[] a)
	{
		//SmC_Picture origPic = new SmC_Picture(picture);
		
		if (lastSeamRemovalWasVertical)
		{
		SmC_Picture newPic = new SmC_Picture(height(), width());
		for(int x = 0; x < width(); x++)
		{
			for(int y = 0; y < height(); y++)
			{
				newPic.set(y, x, picture.get(x, y));
			}
		}
		picture = newPic;
		
		
		//lastSeamRemovalWasVertical = false;
		}
		
		
		System.out.println(a.length + "  "+ picture.width());
		
		//picture.show();
		removeVerticalSeam(a);
		
		
		SmC_Picture backToNormal = new SmC_Picture(height(), width());
		
		for(int x = 0; x < width(); x++)
		{
			for(int y = 0; y < height(); y++)
			{
				backToNormal.set(y, x, picture.get(x, y));
			}
		}
		
		
		/*for(int y = 0; y < height(); y++)
		{
			for(int x = 0; x < width(); x++)
			{
				backToNormal.set(x, y, picture.get(y, x));
			}
		}*/
		
		double[][] newArray = new double[energy[0].length][energy.length];
		for(int i = 0; i < energy.length; i++)
		{
			for(int j = 0; j < energy[0].length; j++)
			{
				newArray[j][i] = energy[i][j];
			}
		}
		energy = newArray;
		
		picture = backToNormal;
	}

	public void removeVerticalSeam(int[] a)
	{
		if(a == null)
		{
			throw new java.lang.NullPointerException();
		}
		if(width() <= 1)
		{
			throw new java.lang.IllegalArgumentException();
		}
		
		for(int i = 0; i < a.length-1; i++)
		{
			if(a[i+1] - a[i] < -1 || a[i+1] - a[i] > 1)
			{
				throw new java.lang.IllegalArgumentException();
			}
			if(a[i] < 0 || a[i] >= width())
			{
				throw new java.lang.IllegalArgumentException();
			}
		}
		if(a[a.length-1] < 0 || a[a.length-1] >= width())
		{
			throw new java.lang.IllegalArgumentException();
		}
		if(a.length != height())
		{
			throw new java.lang.IllegalArgumentException();
		}
		/*
		if(width() <= 1)
		{
			throw new java.lang.IllegalArgumentException();
		}*/
		
		//SmC_Picture newPic = new SmC_Picture(width()-1, height());
		
		
		
	
		for(int y = 0; y < height(); y++)
		{
			for(int x = 0; x < width()-1; x++)
			{
				/*if(x < a[y])
				{
					newPic.set(x, y, picture.get(x, y));
				}*/
				if(x >= a[y])
				{
					picture.set(x, y, picture.get(x+1, y));
				}
				/*else if(x == width()-1)
				{
					picture.set(x, y, new Color(255,255,255));
				}*/
			}
		}
		picture = new SmC_Picture(picture.getImage().getSubimage(0, 0, width()-1, height()));
		//picture = newPic;
		
		double[][] newArr = new double[height()][width()];
		for(int y = 0; y < height(); y++)
		{
			for(int x = 0; x < width(); x++)
			{
				newArr[y][x] = energy(x, y);
			}
		}
		energy = newArr;
				
	}
	
	public static void main(String[] args)
	{
		SmC_Picture pic = new SmC_Picture("testInput/10x12.png");
		SeamCarver test = new SeamCarver(pic);
		int[] seam = test.findVerticalSeam();
		
		for(int i = 0; i < seam.length; i++)
		{
			System.out.print(seam[i] + "    ");
		}
		System.out.println();
		System.out.println();
		for(int i = 0; i < 6; i++)
		{
			System.out.print(i+",4 " + test.picture.get(i, 4) + "    ");
		}
		System.out.println();
		System.out.println();
		test.removeVerticalSeam(seam);
		
		for(int i = 0; i < 5; i++)
		{
			System.out.print(i+",4 " + test.picture.get(i, 4) + "    ");
		}
		System.out.println();
		System.out.println();
		
		System.out.println("Should be [r=163,g=166,b=246]");
		System.out.println("Is " + test.picture().get(1, 4));
		
	}
}