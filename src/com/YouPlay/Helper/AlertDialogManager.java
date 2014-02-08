package com.YouPlay.Helper;


import android.R;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class AlertDialogManager {
	
	public void showAlertDialog(Context context,String title,String message,Boolean status)
	{

	AlertDialog alertDialog = new AlertDialog.Builder(context).create();
	alertDialog.setTitle(title);
	alertDialog.setMessage(message);
//	if(status!=null)
//		alertDialog.setIcon((status) ? R.drawable.sucess : R.drawable.fail;
	alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			
		}
	});
	alertDialog.show();
	}

}
