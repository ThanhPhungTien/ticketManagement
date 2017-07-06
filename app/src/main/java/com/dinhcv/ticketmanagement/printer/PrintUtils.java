package com.dinhcv.ticketmanagement.printer;

import android.content.res.Resources;

import com.android.print.sdk.Barcode;
import com.android.print.sdk.PrinterConstants.BarcodeType;
import com.android.print.sdk.PrinterConstants.Command;
import com.android.print.sdk.PrinterInstance;
import com.dinhcv.ticketmanagement.R;
import com.dinhcv.ticketmanagement.model.Settings;
import com.dinhcv.ticketmanagement.model.structure.TicketInfo;
import com.dinhcv.ticketmanagement.utils.Debug;
import com.dinhcv.ticketmanagement.utils.Utils;

import java.util.Date;

public class PrintUtils {


	public static void printBill(Resources resources, PrinterInstance mPrinter, TicketInfo ticketInfo) {
		mPrinter.init();
		mPrinter.setEncoding("UTF-8");
		StringBuffer sb = new StringBuffer();
		// mPrinter.setPrinter(BluetoothPrinter.COMM_LINE_HEIGHT, 80);

		mPrinter.setPrinter(Command.ALIGN, Command.ALIGN_CENTER);
		mPrinter.setCharacterMultiple(0, 0);

		String parking = Settings.getParking();
		String address = Settings.getAddress();
		String hotline = Settings.getHotline();
		String website = Settings.getWebsite();

		if (parking != null) {
			sb.append(parking + "\n");
		}
		mPrinter.setPrinter(Command.ALIGN, Command.ALIGN_LEFT);
		if (address != null) {
			sb.append(resources.getString(R.string.add_name)+" "+address + "\n");
		}
		if (hotline != null) {
			sb.append(resources.getString(R.string.hot_line_name)+" "+hotline + "\n");
		}

		if (website != null) {
			sb.append(resources.getString(R.string.web_name)+" "+website + "\n");
		}


		sb.append("==============================\n");
		mPrinter.printText(sb.toString());

		mPrinter.setPrinter(Command.ALIGN, Command.ALIGN_CENTER);
		mPrinter.setCharacterMultiple(0, 1);
		mPrinter.printText(resources.getString(R.string.shop_thanks1) +"\n");

		mPrinter.setCharacterMultiple(0, 0);

		// 字号使用默认

		mPrinter.setPrinter(Command.ALIGN, Command.ALIGN_LEFT);
		String timein = Utils.convertDateInToString(ticketInfo.getTimeIn());
		String timeout = Utils.convertDateInToString(ticketInfo.getTimeOut());
		mPrinter.printText(resources.getString(R.string.time_in) +" "+ timein +"\n");
		mPrinter.printText(resources.getString(R.string.time_out) +" " + timeout+ "\n");

		String totalTime = Utils.getTotalTime(ticketInfo.getTimeIn(), ticketInfo.getTimeOut());
		mPrinter.printText(resources.getString(R.string.time_total) +" "+ totalTime+ "\n");
		String fee = Utils.convertFeeToString(ticketInfo.getFee());
		mPrinter.printText(resources.getString(R.string.shop_print_time) +" "+ fee+ "\n");

		mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 3);
	}



	public static void printTicket(Resources resources, PrinterInstance mPrinter, TicketInfo ticketInfo) {
		mPrinter.init();
		mPrinter.setEncoding("UTF-8");
		StringBuffer sb = new StringBuffer();

		mPrinter.setPrinter(Command.ALIGN, Command.ALIGN_CENTER);
		mPrinter.setCharacterMultiple(0, 0);

		String parking = Settings.getParking();
		String address = Settings.getAddress();
		String hotline = Settings.getHotline();
		String website = Settings.getWebsite();

		if (parking != null) {
			sb.append(parking + "\n");
		}
		mPrinter.setPrinter(Command.ALIGN, Command.ALIGN_LEFT);
		if (address != null) {
			sb.append(resources.getString(R.string.add_name)+" "+address + "\n");
		}
		if (hotline != null) {
			sb.append(resources.getString(R.string.hot_line_name)+" "+hotline + "\n");
		}

		if (website != null) {
			sb.append(resources.getString(R.string.web_name)+" "+website + "\n");
		}
		sb.append("==============================\n");

		mPrinter.printText(sb.toString());
		mPrinter.setPrinter(Command.ALIGN, Command.ALIGN_CENTER);
		mPrinter.setCharacterMultiple(0, 1);
		mPrinter.printText(resources.getString(R.string.shop_thanks) +"\n");

		mPrinter.setPrinter(Command.ALIGN, Command.ALIGN_LEFT);
		// 字号使用默认
		mPrinter.setCharacterMultiple(0, 0);

		String timein = Utils.convertDateInToString(ticketInfo.getTimeIn());
		mPrinter.printText(resources.getString(R.string.time_in) +" "+ timein +"\n");


		mPrinter.setPrinter(Command.ALIGN, Command.ALIGN_CENTER);
		// Code128
		String lisence = ticketInfo.getLisenceCode();
		Barcode barcode = new Barcode(BarcodeType.CODE128, 2, 80, 2, lisence);
		mPrinter.printBarCode(barcode);

		mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 3);
	}


}
