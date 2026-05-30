package com.daizuongkk.building.utils;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor

public class ImageUtils {
	public static String detectMimeType(byte[] image) {
		if (image.length < 4)
			return "image/jpeg";

		// PNG: 89 50 4E 47
		if ((image[0] & 0xFF) == 0x89 && image[1] == 0x50 && image[2] == 0x4E && image[3] == 0x47) {
			return "image/png";
		}

		// JPEG: FF D8
		if ((image[0] & 0xFF) == 0xFF && (image[1] & 0xFF) == 0xD8) {
			return "image/jpeg";
		}

		// WEBP: "RIFF....WEBP"
		if (image.length >= 12 &&
				image[0] == 'R' && image[1] == 'I' &&
				image[8] == 'W' && image[9] == 'E' && image[10] == 'B' && image[11] == 'P') {
			return "image/webp";
		}

		return "image/jpeg"; // fallback
	}

}
