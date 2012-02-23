package com.applicake.beanstalkclient.permissions;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import android.content.Context;

public class PermissionsPersistenceUtil {

  private static final String DATA_DIR = "/data/data/";
  private static final String FILENAME = "permissions.ser";
  private Context mContext;

  public PermissionsPersistenceUtil(Context context) {
    this.mContext = context;
  }

  public PermissionsData readStoredPermissionsData() throws IOException {
    ObjectInput input = null;
    try {
      InputStream file = new FileInputStream(generateFileName());
      InputStream buffer = new BufferedInputStream(file);
      input = new ObjectInputStream(buffer);
      PermissionsData permissionsData = (PermissionsData) input.readObject();
      return permissionsData;
    } catch (Exception e) {
      e.printStackTrace();
      throw new PermissionsPersistenceException();
    } finally {
      input.close();
    }
  }

  public void savePermissionsDataToFile(PermissionsData data) throws IOException {
    ObjectOutput output = null;
    try {
      OutputStream file = new FileOutputStream(generateFileName());
      OutputStream buffer = new BufferedOutputStream(file);
      output = new ObjectOutputStream(buffer);
      output.writeObject(data);
    } catch (Exception e) {
      e.printStackTrace();
      throw new PermissionsPersistenceException();
    } finally {
      if (output != null) {
        output.close();
      }
    }
  }
  
  private String generateFileName() {
    return DATA_DIR + mContext.getPackageName() + "/" + FILENAME;
  }
}
