package com.iyuba.conceptEnglish.sqlite.compator;

import java.util.Comparator;

import com.iyuba.conceptEnglish.sqlite.mode.Voa;

public class VoaCompator implements Comparator<Voa>{

	public int compare(Voa voa1, Voa voa2) {
		if(voa1.titleFind < voa2.titleFind) {
			return 1;
		} else if(voa1.titleFind == voa2.titleFind) {
			if(voa1.titleFind < voa2.titleFind) {
				return 1;
			} else if(voa1.titleFind == voa2.titleFind) {
				return 0;
			} else {
				return -1;
			}
		} else {
			return -1;
		}
	}
}
