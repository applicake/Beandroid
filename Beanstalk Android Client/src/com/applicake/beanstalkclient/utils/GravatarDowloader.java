package com.applicake.beanstalkclient.utils;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.ImageView;

public class GravatarDowloader {

	private static GravatarDowloader instance;

	// initialize instance or return an existing one
	public static GravatarDowloader getInstance() {
		if (instance == null) {
			instance = new GravatarDowloader();
		}
		return instance;
	}


	// convert user email to gravatarId
	public static final String md5(final String s) {
		try {
			// Create MD5 Hash from lower case string
			MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
			try {
				digest.update(s.toLowerCase().trim().getBytes("CP1252"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			byte messageDigest[] = digest.digest();

			// Create Hex String
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < messageDigest.length; i++) {
				String h = Integer.toHexString(0xFF & messageDigest[i]);
				while (h.length() < 2)
					h = "0" + h;
				hexString.append(h);
			}
			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}

	public void download(String userEmail, ImageView targetImageView) {

		String url = "http://www.gravatar.com/avatar.php?gravatar_id=" + md5(userEmail)
				+ "&size=80&d=mm";
		resetPurgeTimer();
		Bitmap bitmap = getBitmapFromCache(url);

		if (bitmap == null) {
			if (cancelPotentialDownload(url, targetImageView)) {
				BitmapDownloaderTask task = new BitmapDownloaderTask(targetImageView);
				DownloadedDrawable downloadedDrawable = new DownloadedDrawable(task);
				targetImageView.setImageDrawable(downloadedDrawable);
				task.execute(url);
			}
		} else {
			cancelPotentialDownload(url, targetImageView);
			targetImageView.setImageBitmap(bitmap);
		}

	}

	static Bitmap downloadBitmap(String urlStr) {
		InputStream is = null;
		try {
			URL url = new URL(urlStr);
			URLConnection conn = url.openConnection();
			conn.connect();
			is = conn.getInputStream();

			final Bitmap bitmap = BitmapFactory.decodeStream(new FlushedInputStream(is));
			return bitmap;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return null;
	}

	private boolean cancelPotentialDownload(String url, ImageView targetImageView) {
		BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(targetImageView);

		if (bitmapDownloaderTask != null) {
			String bitmapUrl = bitmapDownloaderTask.url;
			if ((bitmapUrl == null) || (!bitmapUrl.equals(url))) {
				bitmapDownloaderTask.cancel(true);
			} else {
				// the same URL is already being downloaded
				return false;
			}
		}
		return true;
	}

	public BitmapDownloaderTask getBitmapDownloaderTask(ImageView imageView) {
		if (imageView != null) {
			Drawable drawable = imageView.getDrawable();
			if (drawable instanceof DownloadedDrawable) {
				DownloadedDrawable downloadedDrawable = (DownloadedDrawable) drawable;
				return downloadedDrawable.getBitmapDownloaderTask();
			}
		}
		return null;

	}

	// custom implementation of FilterInputStream - needed to fix
	// BitmapFactory's bug occuring with slower connections

	static class FlushedInputStream extends FilterInputStream {

		public FlushedInputStream(InputStream inputStream) {
			super(inputStream);
		}

		@Override
		public long skip(long n) throws IOException {
			long totalBytesSkipped = 0L;
			while (totalBytesSkipped < n) {
				long bytesSkipped = in.skip(n - totalBytesSkipped);
				if (bytesSkipped == 0L) {
					int byt = read();
					if (byt < 0) {
						break; // we reached EOF
					} else {
						bytesSkipped = 1; // we read one byte
					}
				}
				totalBytesSkipped += bytesSkipped;
			}
			return totalBytesSkipped;
		}
	}

	// a class that will contain the downloaded image - before the image is
	// downloaded, it will show a colored field

	static class DownloadedDrawable extends ColorDrawable {
		private final WeakReference<BitmapDownloaderTask> bitmapDownloaderTaskReference;

		// constructor
		public DownloadedDrawable(BitmapDownloaderTask task) {
			super(Color.WHITE);
			bitmapDownloaderTaskReference = new WeakReference<BitmapDownloaderTask>(task);
		}

		// getter
		public BitmapDownloaderTask getBitmapDownloaderTask() {
			return bitmapDownloaderTaskReference.get();
		}

	}

	class BitmapDownloaderTask extends AsyncTask<String, Void, Bitmap> {

		private String url;

		private final WeakReference<ImageView> imageViewReference;

		public BitmapDownloaderTask(ImageView imageView) {
			imageViewReference = new WeakReference<ImageView>(imageView);
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			url = params[0];
			return downloadBitmap(url);
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if (isCancelled()) {
				bitmap = null;
			}

			addBitmapToCache(url, bitmap);

			if (imageViewReference != null) {
				ImageView imageView = imageViewReference.get();
				BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);
				// Change bitmap only if this process is still associated with
				// it
				if (this == bitmapDownloaderTask) {
					imageView.setImageBitmap(bitmap);
				}
			}
		}

	}

	/*
	 * Cache-related fields and methods.
	 */

	/** The Constant HARD_CACHE_CAPACITY. */
	private static final int HARD_CACHE_CAPACITY = 10;

	/** The Constant DELAY_BEFORE_PURGE. */
	private static final int DELAY_BEFORE_PURGE = 10 * 1000; // in milliseconds

	private Bitmap getBitmapFromCache(String url) {
		// First try the hard reference cache
		synchronized (sHardBitmapCache) {
			final Bitmap bitmap = sHardBitmapCache.get(url);
			if (bitmap != null) {
				// Bitmap found in hard cache
				// Move element to first position, so that it is removed last
				sHardBitmapCache.remove(url);
				sHardBitmapCache.put(url, bitmap);
				return bitmap;
			}
		}

		// Then try the soft reference cache
		SoftReference<Bitmap> bitmapReference = sSoftBitmapCache.get(url);
		if (bitmapReference != null) {
			final Bitmap bitmap = bitmapReference.get();
			if (bitmap != null) {
				// Bitmap found in soft cache
				return bitmap;
			} else {
				// Soft reference has been Garbage Collected
				sSoftBitmapCache.remove(url);
			}
		}

		return null;
	}

	public void addBitmapToCache(String url, Bitmap bitmap) {
		if (bitmap != null) {
			synchronized (sHardBitmapCache) {
				sHardBitmapCache.put(url, bitmap);
			}
		}

	}

	// Hard cache, with a fixed maximum capacity and a life duration
	/** The s hard bitmap cache. */
	private final HashMap<String, Bitmap> sHardBitmapCache = new LinkedHashMap<String, Bitmap>(
			HARD_CACHE_CAPACITY / 2, 0.75f, true) {
		/**
				 * 
				 */
		private static final long serialVersionUID = -6146534497815359245L;

		@Override
		protected boolean removeEldestEntry(LinkedHashMap.Entry<String, Bitmap> eldest) {
			if (size() > HARD_CACHE_CAPACITY) {
				// Entries push-out of hard reference cache are transferred to
				// soft reference cache
				sSoftBitmapCache.put(eldest.getKey(),
						new SoftReference<Bitmap>(eldest.getValue()));
				return true;
			} else
				return false;
		}
	};

	// Soft cache for bitmaps kicked out of hard cache
	/** The Constant sSoftBitmapCache. */
	private final static ConcurrentHashMap<String, SoftReference<Bitmap>> sSoftBitmapCache = new ConcurrentHashMap<String, SoftReference<Bitmap>>(
			HARD_CACHE_CAPACITY / 2);

	/** The purge handler. */
	private final Handler purgeHandler = new Handler();

	/** The purger. */
	private final Runnable purger = new Runnable() {
		public void run() {
			clearCache();
		}
	};

	/**
	 * Adds this bitmap to the cache.
	 * 
	 * @param url
	 *            the url
	 * @param bitmap
	 *            The newly downloaded bitmap.
	 */

	/**
	 * Clears the image cache used internally to improve performance. Note that
	 * for memory efficiency reasons, the cache will automatically be cleared
	 * after a certain inactivity delay.
	 */
	public void clearCache() {
		sHardBitmapCache.clear();
		sSoftBitmapCache.clear();
	}

	/**
	 * Allow a new delay before the automatic cache clear is done.
	 */
	private void resetPurgeTimer() {
		purgeHandler.removeCallbacks(purger);
		purgeHandler.postDelayed(purger, DELAY_BEFORE_PURGE);
	}

}
