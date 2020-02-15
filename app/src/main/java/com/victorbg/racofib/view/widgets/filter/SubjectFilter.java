package com.victorbg.racofib.view.widgets.filter;

import com.victorbg.racofib.data.model.subject.Subject;

public class SubjectFilter {

  public Subject subject;
  public boolean checked = true;

  public void changeChecked() {
    this.checked = !this.checked;
  }
}
