package me.adrianperez.wheelofnews;

import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import java.util.Optional;
import com.vaadin.flow.component.dependency.CssImport;

@CssImport("./styles/styles.css")
@Route("")
public class MainView extends VerticalLayout {

    private final NewsArticleService newsArticleService;
    private final Span resultLabel;
    private boolean spinning = false;

    private static final String NEWS_HTML_TEMPLATE = "🎉 Selected News: <a href='%s' target='_blank'>%s</a>";

    public MainView(NewsArticleService newsArticleService) {
        this.newsArticleService = newsArticleService;
        setWidth("100%");
        setHeight("100vh");
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        H1 title = createTitle();
        Div wheelContainer = createWheelContainer();
        Div wheel = createWheel();
        wheelContainer.add(wheel);
        Button spinButton = createSpinButton(wheel);
        resultLabel = createResultLabel();

        add(title, wheelContainer, spinButton, resultLabel);
    }

    private H1 createTitle() {
        return new H1("Wheel of News");
    }

    private Div createWheelContainer() {
        Div wheelContainer = new Div();
        wheelContainer.setClassName("wheel-container");

        return wheelContainer;
    }

    private Div createWheel() {
        Div wheel = new Div();
        wheel.setClassName("wheel");

        return wheel;
    }

    private Button createSpinButton(Div wheel) {
        return new Button("Spin the Wheel", e -> spinWheel(wheel));
    }

    private Span createResultLabel() {
        return new Span();
    }

    private void spinWheel(Div wheel) {
        if (spinning) {
            return;
        }

        spinning = true;

        // JavaScript with callback to synchronize timing sequences
        wheel.getElement().executeJs(
                "this.classList.add('spinning');" +
                        "setTimeout(() => {" +
                        "   this.classList.remove('spinning');" +
                        "   $0.$server.endSpin();" +
                        "}, 3000);",
                getElement()
        );
    }

    @ClientCallable
    private void endSpin() {
        getUI().ifPresent(ui -> ui.access(() -> {
            showResult();
            spinning = false;
        }));
    }

    private void showResult() {
        Optional<NewsArticle> newsArticle = newsArticleService.getRandomNews();
        newsArticle.ifPresentOrElse(this::formatSelectedNews, this::showNoNewsMessage);
    }

    private void formatSelectedNews(NewsArticle article) {
        String title = article.getTitle();
        String url = article.getUrl();
        resultLabel.setText("🎉 Selected News: " + title);
        resultLabel.getElement().setProperty("innerHTML", String.format(NEWS_HTML_TEMPLATE, url, title));
    }

    private void showNoNewsMessage() {
        resultLabel.setText("No news found for today or yesterday.");
    }
}

