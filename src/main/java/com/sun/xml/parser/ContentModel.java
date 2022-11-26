package com.sun.xml.parser;

final class ContentModel {
    public char type;
    public Object content;
    public ContentModel next;
    private SimpleHashtable cache = new SimpleHashtable();

    public ContentModel(String paramString) {
        this.type = Character.MIN_VALUE;
        this.content = paramString;
    }

    public ContentModel(char paramChar, ContentModel paramContentModel) {
        this.type = paramChar;
        this.content = paramContentModel;
    }

    public boolean empty() {
        ContentModel contentModel;
        switch (this.type) {
            case '*' :
            case '?' :
                return true;
            case '\000' :
            case '+' :
                return false;
            case '|' :
                if (this.content instanceof ContentModel && ((ContentModel) this.content).empty())
                    return true;
                contentModel = this.next;
                for (; contentModel != null; contentModel = contentModel.next) {
                    if (contentModel.empty())
                        return true;
                }
                return false;
            case ',' :
                if (this.content instanceof ContentModel) {
                    if (!((ContentModel) this.content).empty())
                        return false;
                } else {
                    return false;
                }
                contentModel = this.next;
                for (; contentModel != null; contentModel = contentModel.next) {
                    if (!contentModel.empty())
                        return false;
                }
                return true;
        }
        throw new InternalError();
    }

    public boolean first(String paramString) {
        boolean bool1;
        Boolean bool = (Boolean) this.cache.get(paramString);
        if (bool != null)
            return bool.booleanValue();
        switch (this.type) {
            case '\000' :
            case '*' :
            case '+' :
            case '?' :
                if (this.content instanceof String) {
                    boolean bool2 = (this.content != paramString) ? false : true;
                    break;
                }
                bool1 = ((ContentModel) this.content).first(paramString);
                break;
            case ',' :
                if (this.content instanceof String) {
                    bool1 = !(this.content != paramString);
                    break;
                }
                if (((ContentModel) this.content).first(paramString)) {
                    bool1 = true;
                    break;
                }
                if (!((ContentModel) this.content).empty()) {
                    bool1 = false;
                    break;
                }
                if (this.next != null) {
                    bool1 = this.next.first(paramString);
                    break;
                }
                bool1 = false;
                break;
            case '|' :
                if (this.content instanceof String && this.content == paramString) {
                    bool1 = true;
                    break;
                }
                if (((ContentModel) this.content).first(paramString)) {
                    bool1 = true;
                    break;
                }
                if (this.next != null) {
                    bool1 = this.next.first(paramString);
                    break;
                }
                bool1 = false;
                break;
            default :
                throw new InternalError();
        }
        if (bool1) {
            this.cache.put(paramString, Boolean.TRUE);
        } else {
            this.cache.put(paramString, Boolean.FALSE);
        }
        return bool1;
    }
}
