package com.victorbg.racofib.view.widgets.bottom;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

public class MaterialBottomSheetDialogFragment extends AppCompatDialogFragment {

  @NonNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    return new MaterialBottomSheet(getContext(), getTheme());
  }
}
