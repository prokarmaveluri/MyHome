
package com.dignityhealth.myhome.app.mvp;

public interface BaseView<T extends BasePresenter> {

    void setPresenter(T presenter);

}
