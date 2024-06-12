// Generated by view binder compiler. Do not edit!
package tlu.cse.android.ht63.dogcare.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;
import tlu.cse.android.ht63.dogcare.R;

public final class ActivityForgotPasswordBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final AppCompatButton btResetPassword;

  @NonNull
  public final AppCompatEditText edtEmail;

  @NonNull
  public final ImageView icControl;

  @NonNull
  public final ImageView imgBack;

  @NonNull
  public final ConstraintLayout layoutOfHeader;

  @NonNull
  public final ConstraintLayout main;

  @NonNull
  public final TextView tvEnd;

  @NonNull
  public final TextView tvTitle;

  @NonNull
  public final TextView tvTitle1;

  private ActivityForgotPasswordBinding(@NonNull ConstraintLayout rootView,
      @NonNull AppCompatButton btResetPassword, @NonNull AppCompatEditText edtEmail,
      @NonNull ImageView icControl, @NonNull ImageView imgBack,
      @NonNull ConstraintLayout layoutOfHeader, @NonNull ConstraintLayout main,
      @NonNull TextView tvEnd, @NonNull TextView tvTitle, @NonNull TextView tvTitle1) {
    this.rootView = rootView;
    this.btResetPassword = btResetPassword;
    this.edtEmail = edtEmail;
    this.icControl = icControl;
    this.imgBack = imgBack;
    this.layoutOfHeader = layoutOfHeader;
    this.main = main;
    this.tvEnd = tvEnd;
    this.tvTitle = tvTitle;
    this.tvTitle1 = tvTitle1;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityForgotPasswordBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityForgotPasswordBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_forgot_password, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityForgotPasswordBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.bt_reset_password;
      AppCompatButton btResetPassword = ViewBindings.findChildViewById(rootView, id);
      if (btResetPassword == null) {
        break missingId;
      }

      id = R.id.edt_email;
      AppCompatEditText edtEmail = ViewBindings.findChildViewById(rootView, id);
      if (edtEmail == null) {
        break missingId;
      }

      id = R.id.ic_control;
      ImageView icControl = ViewBindings.findChildViewById(rootView, id);
      if (icControl == null) {
        break missingId;
      }

      id = R.id.img_back;
      ImageView imgBack = ViewBindings.findChildViewById(rootView, id);
      if (imgBack == null) {
        break missingId;
      }

      id = R.id.layout_of_header;
      ConstraintLayout layoutOfHeader = ViewBindings.findChildViewById(rootView, id);
      if (layoutOfHeader == null) {
        break missingId;
      }

      ConstraintLayout main = (ConstraintLayout) rootView;

      id = R.id.tv_end;
      TextView tvEnd = ViewBindings.findChildViewById(rootView, id);
      if (tvEnd == null) {
        break missingId;
      }

      id = R.id.tv_title;
      TextView tvTitle = ViewBindings.findChildViewById(rootView, id);
      if (tvTitle == null) {
        break missingId;
      }

      id = R.id.tv_title1;
      TextView tvTitle1 = ViewBindings.findChildViewById(rootView, id);
      if (tvTitle1 == null) {
        break missingId;
      }

      return new ActivityForgotPasswordBinding((ConstraintLayout) rootView, btResetPassword,
          edtEmail, icControl, imgBack, layoutOfHeader, main, tvEnd, tvTitle, tvTitle1);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
