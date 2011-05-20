package com.applicake.beanstalkclient.handlers;

import java.util.ArrayList;
import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.applicake.beanstalkclient.Permission;
import com.applicake.beanstalkclient.Repository;

public class PermissionsHandler extends DefaultHandler {

	private ArrayList<Permission> permissionList;
	private Permission permission;
	private StringBuilder buffer = new StringBuilder();

	@Override
	public void startElement(String namespaceURI, String localName, String qName,
			Attributes atts) throws SAXException {
		buffer.setLength(0);

		if (localName.equals("permissions")) {
			permissionList = new ArrayList<Permission>();
		}
		if (localName.equals("permission")) {
			permission = new Permission();
		}

	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (permission != null) {
			if (localName.equals("permission")) {
				permissionList.add(permission);
			}

			if (localName.equals("full-deployments-access")) {
				permission
						.setFullDeploymentAccess(Boolean.parseBoolean(buffer.toString()));
			}

			if (localName.equals("id")) {

				try {
					permission.setId(Integer.parseInt(buffer.toString()));
				} catch (NumberFormatException nfe) {
					throw new SAXException(nfe);
				}
			}

			if (localName.equals("repository-id")) {

				try {
					permission.setRepositoryId(Integer.parseInt(buffer.toString()));
				} catch (NumberFormatException nfe) {
					throw new SAXException(nfe);
				}
			}

			if (localName.equals("server-environment-id")) {
				if (buffer.length() < 0) {

					try {
						permission.setServerEnvironmentId(Integer.parseInt(buffer
								.toString()));
					} catch (NumberFormatException nfe) {
						throw new SAXException(nfe);
					}
				}

			}

			if (localName.equals("user-id")) {

				try {
					permission.setUserId(Integer.parseInt(buffer.toString()));
				} catch (NumberFormatException nfe) {
					throw new SAXException(nfe);
				}
			}

			if (localName.equals("read")) {
				permission.setReadAccess(Boolean.parseBoolean(buffer.toString()));
			}

			if (localName.equals("write")) {
				permission.setWriteAccess(Boolean.parseBoolean(buffer.toString()));
			}
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) {
		buffer.append(ch, start, length);
	}

	public ArrayList<Permission> retrievePermissionList() throws SAXException {
		if (permissionList == null)
			throw new SAXException("Error while parsing permission list");
		return permissionList;

	}

	public HashMap<Integer, Permission> retrievePermissionHashMap() {
		HashMap<Integer, Permission> repoHashMap = new HashMap<Integer, Permission>();
		for (Permission r : permissionList){
			repoHashMap.put(r.getRepositoryId(), r);
		}
		return repoHashMap;
	}
}
