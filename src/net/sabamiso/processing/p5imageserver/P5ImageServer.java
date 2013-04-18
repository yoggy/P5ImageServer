package net.sabamiso.processing.p5imageserver;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Locale;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import processing.core.PImage;

public class P5ImageServer extends Thread {
	String url;
	String name;
	int last_status_code = -1;
	int interval = 100;
	byte[] jpeg_data;

	public P5ImageServer(String url, String name) {
		this.url = url;
		this.name = name;
		start();
	}

	public int getInterval() {
		return interval;
	}
	
	public void setInterval(int val) {
		this.interval = val;
	}
	
	public int getLastStatusCode() {
		return last_status_code;
	}

	synchronized void setJpegData(byte[] jpeg_data) {
		this.jpeg_data = jpeg_data;
	}

	synchronized byte[] getJpegData() {
		return jpeg_data;
	}

	public boolean send(PImage pimage) {
		if (pimage == null)
			return false;

		pimage.loadPixels();

		try {
			BufferedImage buffered_image = new BufferedImage(pimage.width,
					pimage.height, BufferedImage.TYPE_3BYTE_BGR);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ImageOutputStream ios = ImageIO.createImageOutputStream(bos);

			for (int y = 0; y < pimage.height; y++) {
				for (int x = 0; x < pimage.width; x++) {
					buffered_image.setRGB(x, y, pimage.pixels[x + y
							* pimage.width]);
				}
			}

			JPEGImageWriteParam param = new JPEGImageWriteParam(
					Locale.getDefault());
			param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			param.setCompressionQuality(0.9f);
			ImageWriter iw = ImageIO.getImageWritersByFormatName("jpg").next();
			iw.setOutput(ios);
			iw.write(null, new IIOImage(buffered_image, null, null), param);
			setJpegData(bos.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public void run() {
		while (true) {
			try {
				byte [] jpeg_data = getJpegData();
				
				if (jpeg_data == null) continue;

				HttpClient http = new DefaultHttpClient();
				HttpPost post = new HttpPost(url);

				MultipartEntity ent = new MultipartEntity();

				FormBodyPart bodyPart = new FormBodyPart("name",
						new StringBody(this.name));
				ent.addPart(bodyPart);

				ByteArrayInputStream bais = new ByteArrayInputStream(
						jpeg_data);
				InputStreamBody isb = new InputStreamBody(bais, "image/jpeg",
						"jpeg_image.jpg");
				ent.addPart("image", isb);

				post.setEntity(ent);

				HttpResponse res = http.execute(post);
				last_status_code = res.getStatusLine().getStatusCode();
			} catch (Exception e) {
				last_status_code = -1;
				e.printStackTrace();
			}
			
			try {
				sleep(interval);
			} catch (InterruptedException e) {
			}
		}
	}
}
