package com.sun.xml.parser;

class ContentModelState {
    private ContentModel model;
    private boolean sawOne;
    private ContentModelState next;

    ContentModelState(ContentModel paramContentModel) {
        this(paramContentModel, null);
    }

    private ContentModelState(Object paramObject, ContentModelState paramContentModelState) {
        this.model = (ContentModel) paramObject;
        this.next = paramContentModelState;
        this.sawOne = false;
    }

    boolean terminate() {
        ContentModel contentModel;
        switch (this.model.type) {
            case '+' :
                if (!this.sawOne && !this.model.empty())
                    return false;
            case '*' :
            case '?' :
                return !(this.next != null && !this.next.terminate());
            case '|' :
                return !(!this.model.empty() || (this.next != null && !this.next.terminate()));
            case ',' :
                for (contentModel = this.model; contentModel != null && contentModel.empty(); contentModel = contentModel.next);
                if (contentModel != null)
                    return false;
                return !(this.next != null && !this.next.terminate());
            case '\000' :
                return false;
        }
        throw new InternalError();
    }

    ContentModelState advance(String paramString) throws EndOfInputException {
        ContentModel contentModel;
        switch (this.model.type) {
            case '*' :
            case '+' :
                if (this.model.first(paramString)) {
                    this.sawOne = true;
                    if (this.model.content instanceof String)
                        return this;
                    return (new ContentModelState(this.model.content, this)).advance(paramString);
                }
                if ((this.model.type == '*' || this.sawOne) && this.next != null)
                    return this.next.advance(paramString);
                break;
            case '?' :
                if (this.model.first(paramString)) {
                    if (this.model.content instanceof String)
                        return this.next;
                    return (new ContentModelState(this.model.content, this.next)).advance(paramString);
                }
                if (this.next != null)
                    return this.next.advance(paramString);
                break;
            case '|' :
                for (contentModel = this.model; contentModel != null; contentModel = contentModel.next) {
                    if (contentModel.content instanceof String) {
                        if (paramString == contentModel.content)
                            return this.next;
                    } else if (((ContentModel) contentModel.content).first(paramString)) {
                        return (new ContentModelState(contentModel.content, this.next)).advance(paramString);
                    }
                }
                if (this.model.empty() && this.next != null)
                    return this.next.advance(paramString);
                break;
            case ',' :
                if (this.model.first(paramString)) {
                    ContentModelState contentModelState;
                    if (this.model.type == '\000')
                        return this.next;
                    if (this.model.next == null) {
                        contentModelState = new ContentModelState(this.model.content, this.next);
                    } else {
                        contentModelState = new ContentModelState(this.model.content, this);
                        this.model = this.model.next;
                    }
                    return contentModelState.advance(paramString);
                }
                if (this.model.empty() && this.next != null)
                    return this.next.advance(paramString);
                break;
            case '\000' :
                if (this.model.content == paramString)
                    return this.next;
                break;
        }
        throw new EndOfInputException();
    }
}
