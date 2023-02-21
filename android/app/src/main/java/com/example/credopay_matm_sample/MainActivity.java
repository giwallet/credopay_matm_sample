package com.example.credopay_matm_sample;

import android.content.Intent;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import in.credopay.payment.sdk.CredopayPaymentConstants;
import in.credopay.payment.sdk.PaymentActivity;
import in.credopay.payment.sdk.PaymentManager;
import in.credopay.payment.sdk.Utils;
import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugins.GeneratedPluginRegistrant;
import android.os.Bundle;
import java.util.Objects;

public class MainActivity extends FlutterActivity {

  private static final String CHANNEL = "new_activity";
  public static final String HMAC_SHA256 = "HmacSHA256";
  public static final int MATM_REQUEST_CODE = 121212;
  MethodChannel.Result _result;

  @Override
  public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
    GeneratedPluginRegistrant.registerWith(flutterEngine);

    new MethodChannel(
      flutterEngine.getDartExecutor().getBinaryMessenger(),
      CHANNEL
    )
      .setMethodCallHandler((call, result) -> {
        _result = result;
        if (call.method.equals("start_matm")) {
          int transactionType = call.argument("transType").equals("MPOS")
            ? CredopayPaymentConstants.PURCHASE
            : call.argument("transType").equals("Micro ATM")
              ? CredopayPaymentConstants.MICROATM
              : call.argument("transType").equals("Balance Enquiry")
                ? CredopayPaymentConstants.BALANCE_ENQUIRY
                : call.argument("transType").equals("CASH_AT_POS")
                  ? CredopayPaymentConstants.CASH_AT_POS
                  : call.argument("transType").equals("UPI")
                    ? CredopayPaymentConstants.UPI
                    : CredopayPaymentConstants.VOID;
          boolean change_password = call.argument("change_password");
          int amount = call.argument("amount");
          String clientId = call.argument("clientId");
          String mid = call.argument("mid");
          String mkey = call.argument("mkey");
          String customerMobile = call.argument("mobile");
          String agentId = call.argument("agentId");
          String tid = call.argument("tid");
          Log.d("transactionType", call.argument("transType"));
          Log.d("transactionFilteredNo", Integer.toString(transactionType));
          Log.d(
            "transactionTypeNo",
            Integer.toString(CredopayPaymentConstants.MICROATM)
          );
          startPayment(
            transactionType,
            amount,
            change_password,
            clientId,
            mid,
            mkey,
            customerMobile,
            agentId,
            tid
          );
        } else {
          result.notImplemented();
        }
      });
  }

  public void startPayment(
    int transactionType,
    int amount,
    boolean changePassword,
    String clientId,
    String mid,
    String mKey,
    String customerMobile,
    String agentId,
    String tid
  ) {
    Log.d("loginId", mid);
    Log.d("LOGIN_PASSWORD", mKey);
    Log.d("TRANSACTION_TYPE", Integer.toString(transactionType));

    final Intent intent = new Intent(MainActivity.this, PaymentActivity.class);
    intent.putExtra("TRANSACTION_TYPE", transactionType);
    intent.putExtra("DEBUG_MODE", true);
    intent.putExtra("PRODUCTION", true);
    intent.putExtra("AMOUNT", amount);
    intent.putExtra("LOGIN_ID", mid);
    intent.putExtra("SUCCESS_DISMISS_TIMEOUT", 1L);
    intent.putExtra("MOBILE_NUMBER", customerMobile);
    intent.putExtra("CRN_U", clientId);
    intent.putExtra("CUSTOM_FIELD1", tid);
    intent.putExtra("CUSTOM_FIELD2", clientId);
    intent.putExtra("CUSTOM_FIELD3", agentId);

    if (changePassword) {
      intent.putExtra("LOGIN_PASSWORD", mKey);
    } else {
      intent.putExtra("LOGIN_PASSWORD", mKey);
    }
    intent.putExtra(
      "LOGO",
      Utils.getVariableImage(
        Objects.requireNonNull(
          ContextCompat.getDrawable(
            getApplicationContext(),
            R.drawable.ic_launcher
          )
        )
      )
    );
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    startActivityForResult(intent, MATM_REQUEST_CODE);
  }




  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == MATM_REQUEST_CODE) {
      switch (resultCode) {
        case CredopayPaymentConstants.TRANSACTION_COMPLETED:
          if (data != null) {
            String error = data.getStringExtra("error");
            if (error != null) {
              Log.d("error", error);
            }
            PaymentManager.getInstance().logout();
            Log.d(
              "Response",
              "response : " +
              data.getStringExtra("rrn") +
              " : " +
              data.getStringExtra("card_holder_name")
            );
            String response =
              "{\"rrn\":\"" +
              data.getStringExtra("rrn") +
              "\",\"card_holder_name\":\"" +
              data.getStringExtra("card_holder_name") +
              "\",\"transaction_id\": \"" +
              data.getStringExtra("transaction_id") +
              "\",\"masked_pan\": \"" +
              data.getStringExtra("masked_pan") +
              "\",\"network\": \"" +
              data.getStringExtra("network") +
              "\",\"approval_code\": \"" +
              data.getStringExtra("approval_code") +
              "\",\"card_type\": \"" +
              data.getStringExtra("card_type") +
              "\",\"card_application_name\": \"" +
              data.getStringExtra("card_application_name") +
              "\",\"transaction_type\": \"" +
              data.getStringExtra("transaction_type") +
              "\",\"account_balance\": \"" +
              data.getStringExtra("account_balance") +
              "\"}";
            _result.success(response);
          }
          break;
        case CredopayPaymentConstants.TRANSACTION_CANCELLED:
          if (data != null) {
            String error = data.getStringExtra("error");
            if (error != null) {
              Log.d("TRANSACTION_CANCELLED error", error);
            }
            _result.success("{\"error\":" +true +"\"error\":\"" +error+"}");
          }

          break;
        case CredopayPaymentConstants.VOID_CANCELLED:
          if (data != null) {
            String error = data.getStringExtra("error");
            if (error != null) {
              Log.d("VOID_CANCELLED error", error);
            }
            _result.success("{\"error\":" +true +"\"error\":\"" +error+"}");
          }

          break;
        case CredopayPaymentConstants.LOGIN_FAILED:
          if (data != null) {
            String error = data.getStringExtra("error");
            if (error != null) {
              Log.d("LOGIN_FAILED error", error);
            }
            _result.success(error);
          }
          break;
        case CredopayPaymentConstants.CHANGE_PASSWORD:
          _result.success("CHANGE_PASSWORD");
          break;
        case CredopayPaymentConstants.CHANGE_PASSWORD_SUCCESS:
          //change this code according to your need.
          _result.success("CHANGE_PASSWORD_SUCCESS");
          break;
        case CredopayPaymentConstants.CHANGE_PASSWORD_FAILED:
          if (data != null) {
            String error = data.getStringExtra("error");
            if (error != null) {
              Log.d("CHANGE_PASSWORD_FAILED error", error);
            }
            _result.success("{\"error\":" +true +"\"error\":\"" +error+"}");
          }
        default:
          break;
      }
    } else {
      Log.d("Response", "empty response");
    }
  }
}
