package hr.fer.zemris.crypto.domain;

public class Model {


    private static Model model;
    private String text;

    {
        model = new Model();
    }

    private Model() {
    }

    public static Model getInstance() {
        return model;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
