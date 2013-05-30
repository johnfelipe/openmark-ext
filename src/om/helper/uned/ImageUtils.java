package om.helper.uned;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;

/**
 * Some more imaged-related utility methods, based on javax.imageio.ImageIO class instead of java.awt.RenderKit
 */
public class ImageUtils
{
	/**
	 * @param abData Data for image
	 * @param sMimeType Mime type
	 * @return Dimensions of image as an array of 2 integer values: first value will be width and second one
	 * height, both values will be measured in pixels
	 * @throws Exception
	 */
	public static int[] getImageDimensions(byte[] abData,String sMimeType) throws Exception
	{
		int[] imageDimensions=new int[2];
		imageDimensions[0]=-1;
		imageDimensions[1]=-1;
		Iterator<ImageReader> iter=ImageIO.getImageReadersByMIMEType(sMimeType);
		ImageReader reader=iter.next();
		try
		{
			ImageInputStream stream=new MemoryCacheImageInputStream(new ByteArrayInputStream(abData));
			reader.setInput(stream);
			imageDimensions[0]=reader.getWidth(reader.getMinIndex());
			imageDimensions[1]=reader.getHeight(reader.getMinIndex());
		}
		finally
		{
			reader.dispose();
		}
		return imageDimensions;
	}
}
