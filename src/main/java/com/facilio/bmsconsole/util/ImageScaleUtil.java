package com.facilio.bmsconsole.util;

import org.imgscalr.Scalr;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageScaleUtil {
	
	/**
	 * Resizes an image to a specific size and adds black lines in respect to
	 * the ratio. The resizing will take care of the original ratio.
	 *
	 * @param inputImage - Original input image. Will not be changed.
	 * @param resultWidth - The out coming width
	 * @param resultHeight - The out coming height
	 * @return - The out coming image
	 */
	public static BufferedImage resizeImage(BufferedImage inputImage, int resultWidth, int resultHeight) {
	    // first get the width and the height of the image
	    int originWidth = inputImage.getWidth();
	    int originHeight = inputImage.getHeight();
	    
	    // let us check if we have to scale the image
	    if (originWidth <= resultWidth && originHeight <= resultHeight) {
	        // we don't have to scale the image, just return the origin
	        return inputImage;
	    }

	    // Scale in respect to width or height?
	    Scalr.Mode scaleMode = Scalr.Mode.AUTOMATIC;

	    // find out which side is the shortest
	    int maxSize = 0;
	    if (originHeight > originWidth) {
	        // scale to width
	        scaleMode = Scalr.Mode.FIT_TO_WIDTH;
	        maxSize = resultWidth;
	    } else if (originWidth >= originHeight) {
	        scaleMode = Scalr.Mode.FIT_TO_HEIGHT;
	        maxSize = resultHeight;
	    }

	    // Scale the image to given size
	    BufferedImage outputImage = Scalr.resize(inputImage, Scalr.Method.QUALITY, scaleMode, maxSize);

	    // okay, now let us check that both sides are fitting to our result scaling
	    if (scaleMode.equals(Scalr.Mode.FIT_TO_WIDTH) && outputImage.getHeight() > resultHeight) {
	        // the height is too large, resize again
	        outputImage = Scalr.resize(outputImage, Scalr.Method.QUALITY, Scalr.Mode.FIT_TO_HEIGHT, resultHeight);
	    } else if (scaleMode.equals(Scalr.Mode.FIT_TO_HEIGHT) && outputImage.getWidth() > resultWidth) {
	        // the width is too large, resize again
	        outputImage = Scalr.resize(outputImage, Scalr.Method.QUALITY, Scalr.Mode.FIT_TO_WIDTH, resultWidth);
	    }

	    // now we have an image that is definitely equal or smaller to the given size
	    // Now let us check, which side needs black lines
	    int paddingSize = 0;
	    if (outputImage.getWidth() != resultWidth) {
	        // we need padding on the width axis
	        paddingSize = (resultWidth - outputImage.getWidth()) / 2;
	    } else if (outputImage.getHeight() != resultHeight) {
	        // we need padding on the height axis
	        paddingSize = (resultHeight - outputImage.getHeight()) / 2;
	    }

	    // we need padding?
	    if (paddingSize > 0) {
	        // add the padding to the image
	    	Color col = new Color(255, 255, 255, 80);
	        outputImage = Scalr.pad(outputImage, paddingSize, col);

	        // now we have to crop the image because the padding was added to all sides
	        int x = 0, y = 0, width = 0, height = 0;
	        if (outputImage.getWidth() > resultWidth) {
	            // set the correct range
	            x = paddingSize;
	            y = 0;
	            width = outputImage.getWidth() - (2 * paddingSize);
	            height = outputImage.getHeight();
	        } else if (outputImage.getHeight() > resultHeight) {
	            // set the correct range
	            x = 0;
	            y = paddingSize;
	            width = outputImage.getWidth();
	            height = outputImage.getHeight() - (2 * paddingSize);
	        }
	        
	        // Crop the image 
	        if (width > 0 && height > 0) {
	            outputImage = Scalr.crop(outputImage, x, y, width, height);
	        }
	    }
	    
	    // flush both images
	    inputImage.flush();
	    outputImage.flush();
	    
	    // return the final image
	    return outputImage;
	}
}
