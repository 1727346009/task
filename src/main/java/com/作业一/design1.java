package gdut;

import java.io.*;
import java.util.TreeSet;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import javax.imageio.*;
import javax.swing.*;

public class design1  extends Component {
	DataInputStream data_in;
	private byte[] yuv_array;
	private int[] u_array, v_array;
	private int[] rgb_array;
	private BufferedImage img;
	private int width, height;
	private int frame_number;
    private int frame_size, yuv_frame_size;
    
    public design1(String filename, int width, int height, int frame_number) {
    	this.width = width;
    	this.height = height;
    	frame_size = width * height;
    	this.frame_number = frame_number;
    	yuv_frame_size = (width * height * 3)>>1;
    	//在Heap分配空间
    	img = new BufferedImage(width, height, 1);//1:TYPE_INT_RGB
    	yuv_array = new byte[yuv_frame_size];
		u_array = new int[frame_size];
    	v_array = new int[frame_size];
    	rgb_array = new int[frame_size];
    	
    	try {
    		FileInputStream f_in = new FileInputStream(new File(filename));
    		f_in.skip(frame_number * yuv_frame_size);
    		data_in = new DataInputStream(f_in);
    		data_in.read(yuv_array, 0, yuv_frame_size);
    		this.frame_number++;
    	} catch (IOException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }
    	yuv2rgb();
    	img.setRGB(0, 0, width, height, rgb_array, 0, width);
    }
    
    private void yuv2rgb()
    {
    	int h;
    	int h2;
    	int frame_size2 = frame_size + (frame_size>>2);
    	int width2 = width<<1;
    	int i2, j2;
    	
    	h = 0;
    	h2 = 0;
    	for (int j = 0; j < (height>>1); j++)
    	{
    		for (int i = 0; i < (width>>1); i++)
    		{
    			i2 = i<<1;
    			int a, b;
    			u_array[h2 + i2]   = yuv_array[frame_size + h + i]&0xff;
    			v_array[h2 + i2]   = yuv_array[frame_size2 + h + i]&0xff;
    		}
    		h += width>>1;
    		h2 += width2;
    	}
    	//执行双线性内插，把4:1:1的YUV扩大为4:4:4的YUV
    	h2 = 0;
    	for (j2 = 0; j2 < height - 2; j2 += 2)
    	{
    		for (i2 = 0; i2 < width - 2; i2 += 2)
    		{
    			int a, b, ab;
    			
    			a = u_array[h2 + i2] + u_array[h2 + i2 + 2];//水平
    			b = u_array[h2 + i2] + u_array[h2 + i2 + width2];//垂直
    			ab = u_array[h2 + i2] + u_array[h2 + i2 + 2] + u_array[h2 + i2 + width2] + u_array[h2 + i2 + width2 + 2];//对角线
    			u_array[h2 + i2 + 1] = (a + 1)>>1;
    			u_array[h2 + i2 + width] = (b + 1)>>1;
    			u_array[h2 + i2 + width + 1] = (ab + 2)>>2;
    			
    			a = v_array[h2 + i2] + v_array[h2 + i2 + 2];//水平
    			b = v_array[h2 + i2] + v_array[h2 + i2 + width2];//垂直
    			ab = v_array[h2 + i2] + v_array[h2 + i2 + 2] + v_array[h2 + i2 + width2] + v_array[h2 + i2 + width2 + 2];//对角线
    			v_array[h2 + i2 + 1] = (a + 1)>>1;
    			v_array[h2 + i2 + width] = (b + 1)>>1;
    			v_array[h2 + i2 + width + 1] = (ab + 2)>>2;
    		}
			u_array[h2 + i2 + 1] = u_array[h2 + i2];
			u_array[h2 + i2 + width] = 
			u_array[h2 + i2 + width + 1] = (u_array[h2 + i2] + u_array[h2 + i2 + width2] + 1)>>1;
			
			v_array[h2 + i2 + 1] = v_array[h2 + i2];
			v_array[h2 + i2 + width] = 
			v_array[h2 + i2 + width + 1] = (v_array[h2 + i2] + v_array[h2 + i2 + width2] + 1)>>1;
			
			h2 += width2;
    	}
		for (i2 = 0; i2 < width - 2; i2 += 2)
		{
			int a, b, ab;
			
			u_array[h2 + i2 + 1] = 
			u_array[h2 + i2 + width + 1] = (u_array[h2 + i2] + u_array[h2 + i2 + 2] + 1)>>1;
			u_array[h2 + i2 + width] = u_array[h2 + i2];
			
			v_array[h2 + i2 + 1] = 
			v_array[h2 + i2 + width + 1] = (v_array[h2 + i2] + v_array[h2 + i2 + 2] + 1)>>1;
			v_array[h2 + i2 + width] = v_array[h2 + i2];
		}
		u_array[h2 + i2 + 1] =
		u_array[h2 + i2 + width] = 
		u_array[h2 + i2 + width + 1] = u_array[h2 + i2];   	
    	
		v_array[h2 + i2 + 1] =
		v_array[h2 + i2 + width] = 
		v_array[h2 + i2 + width + 1] = v_array[h2 + i2];
		
		//彩色空间变换,从YUV转换到RGB
		for (int i = 0; i < frame_size; i++)
		{
			int pixel_r, pixel_g, pixel_b;
			int pixel_y = yuv_array[i]&0xff;
			int pixel_u = u_array[i] - 128;
			int pixel_v = v_array[i] - 128;
			//YUV到RGB的矩阵变换运算
			double R = pixel_y - 0.001 * pixel_u + 1.402 * pixel_v;
			double G = pixel_y - 0.344 * pixel_u - 0.714 * pixel_v;
			double B = pixel_y + 1.772 * pixel_u + 0.001 * pixel_v;
			//限幅
			if (R > 255) pixel_r = 255;
			else if (R < 0) pixel_r = 0;
			else pixel_r = (int)R;
			if (G > 255) pixel_g = 255;
			else if (G < 0) pixel_g = 0;
			else pixel_g = (int)G;
			if (B > 255) pixel_b = 255;
			else if (B < 0) pixel_b = 0;
			else pixel_b = (int)B;
			rgb_array[i] = (pixel_r<<16) | (pixel_g<<8) | pixel_b;
		}
    }

    public Dimension getPreferredSize() {
        if (img == null) {
             return new Dimension(width, height);
        } else {
           return new Dimension(img.getWidth(null), img.getHeight(null));
       }
    }
    
    public void writeFile(String formatName, String filename) {
        try {
            ImageIO.write(img, formatName, new File(filename));
        } catch (IOException e) {
            e.printStackTrace();  
        }
    }
    
    public void paint(Graphics g) {
    	g.drawImage(img, 0, 0, null);
    }
    
    public void play(JFrame f) {
    	while(true){
        	try {
        		data_in.read(yuv_array, 0, yuv_frame_size);
        		f.setTitle("YUV Player of GDUT              #" + frame_number + " frames");
        		frame_number++;
        	} catch (IOException e) {  
                // TODO Auto-generated catch block  
                e.printStackTrace();
            }
        	yuv2rgb();
        	img.setRGB(0, 0, width, height, rgb_array, 0, width);
        	repaint(); 
    	}
    }
    
	public static void main(String[] args) {
		// TODO Auto-generated method stub
        JFrame f = new JFrame("YUV Player of GDUT");
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
        });

        
        design1 me = new design1("ShuttleStart_1280x720.yuv", 1280, 720, 10);        
        f.add("Center", me);
        f.pack();
        f.setVisible(true);

        //me.writeFile("jpg", "out.jpg");
        //me.play(f);//连续读取并显示图像
	}

}
