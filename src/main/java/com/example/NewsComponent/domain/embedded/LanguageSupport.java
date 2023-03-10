package com.example.NewsComponent.domain.embedded;

import javax.validation.constraints.NotBlank;

public class LanguageSupport {
    protected String en;
    @NotBlank
    protected String hn;
    protected String pb;


    public LanguageSupport(String en, String hn, String pb) {
        this.en = en;
        this.hn = hn;
        this.pb = pb;
    }

    public LanguageSupport() {
    }

    public String getEn() {
        return this.en;
    }

    public String getHn() {
        return this.hn;
    }

    public String getPb() {
        return this.pb;
    }

    public void setEn(String en) {
        this.en = en;
    }

    public void setHn(String hn) {
        this.hn = hn;
    }

    public void setPb(String pb) {
        this.pb = pb;
    }

}

