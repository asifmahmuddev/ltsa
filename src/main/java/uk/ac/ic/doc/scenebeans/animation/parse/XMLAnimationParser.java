package uk.ac.ic.doc.scenebeans.animation.parse;

import com.sun.xml.parser.Parser;
import com.sun.xml.parser.Resolver;
import com.sun.xml.tree.XmlDocument;
import com.sun.xml.tree.XmlDocumentBuilder;
import java.awt.Component;
import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.xml.sax.DocumentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import uk.ac.ic.doc.natutil.MacroException;
import uk.ac.ic.doc.natutil.MacroExpander;
import uk.ac.ic.doc.scenebeans.CompositeNode;
import uk.ac.ic.doc.scenebeans.Input;
import uk.ac.ic.doc.scenebeans.Layered;
import uk.ac.ic.doc.scenebeans.SceneGraph;
import uk.ac.ic.doc.scenebeans.Style;
import uk.ac.ic.doc.scenebeans.Transform;
import uk.ac.ic.doc.scenebeans.activity.Activity;
import uk.ac.ic.doc.scenebeans.activity.ActivityRunner;
import uk.ac.ic.doc.scenebeans.activity.CompositeActivity;
import uk.ac.ic.doc.scenebeans.activity.ConcurrentActivity;
import uk.ac.ic.doc.scenebeans.activity.SequentialActivity;
import uk.ac.ic.doc.scenebeans.animation.Animation;
import uk.ac.ic.doc.scenebeans.animation.AnnounceCommand;
import uk.ac.ic.doc.scenebeans.animation.Command;
import uk.ac.ic.doc.scenebeans.animation.CompositeCommand;
import uk.ac.ic.doc.scenebeans.animation.EventInvoker;
import uk.ac.ic.doc.scenebeans.animation.ResetActivityCommand;
import uk.ac.ic.doc.scenebeans.animation.SetParameterCommand;
import uk.ac.ic.doc.scenebeans.animation.StartActivityCommand;
import uk.ac.ic.doc.scenebeans.animation.StopActivityCommand;

public class XMLAnimationParser {
    private BeanFactory _factory = new BeanFactory();
    private Map _symbol_table = new HashMap();
    private List _behaviour_links = new ArrayList();
    private List _event_links = new ArrayList();
    private MacroExpander _macro_table = new MacroExpander();
    ValueParser _value_parser;
    private URL _doc_url;
    private Component _component;
    private Animation _anim = null;
    private static final String PROPERTY_ACTIVITY_NAME = "activityName";
    private static final String PI_TARGET = "scenebeans";
    private static final String PI_CODEBASE = "codebase";
    private static final String PI_CATEGORY = "category";
    private static final String PI_PACKAGE = "package";
    private static final String CATEGORY_SCENE = "scene";
    private static final String PKG_SCENE = "uk.ac.ic.doc.scenebeans";
    private static final String CATEGORY_BEHAVIOUR = "behaviour";
    private static final String PKG_BEHAVIOUR = "uk.ac.ic.doc.scenebeans.behaviour";

    public XMLAnimationParser(URL paramURL, Component paramComponent) {
        this._doc_url = paramURL;
        this._value_parser = new ValueParser(paramURL);
        this._component = paramComponent;
        this._factory.addCategory("scene", "", "", true);
        this._factory.addPackage("scene", "uk.ac.ic.doc.scenebeans");
        this._factory.addCategory("behaviour", "", "", true);
        this._factory.addPackage("behaviour", "uk.ac.ic.doc.scenebeans.behaviour");
    }

    public XMLAnimationParser(File paramFile, Component paramComponent) throws MalformedURLException {
        this(paramFile.toURL(), paramComponent);
    }

    public URL getDocumentURL() {
        return this._doc_url;
    }

    public Component getViewComponent() {
        return this._component;
    }

    public void addScenePackage(String paramString) {
        this._factory.addPackage("scene", paramString);
    }

    public void addScenePackage(ClassLoader paramClassLoader, String paramString) {
        this._factory.addPackage("scene", paramClassLoader, paramString);
    }

    public void addBehaviourPackage(String paramString) {
        this._factory.addPackage("behaviour", paramString);
    }

    public void addBehaviourPackage(ClassLoader paramClassLoader, String paramString) {
        this._factory.addPackage("behaviour", paramString);
    }

    public Animation parseAnimation() throws IOException, AnimationParseException {
        try {
            InputSource inputSource = Resolver.createInputSource(this._doc_url, false);
            Parser parser = new Parser();
            XmlDocumentBuilder xmlDocumentBuilder = new XmlDocumentBuilder();
            parser.setDocumentHandler((DocumentHandler) xmlDocumentBuilder);
            parser.parse(inputSource);
            XmlDocument xmlDocument = xmlDocumentBuilder.getDocument();
            xmlDocument.getDocumentElement().normalize();
            return translateDocument((Document) xmlDocument);
        } catch (SAXParseException sAXParseException) {
            throw new AnimationParseException(sAXParseException.getSystemId() + " line  " + sAXParseException.getLineNumber() + ": " + sAXParseException.getMessage());
        } catch (SAXException sAXException) {
            throw new AnimationParseException("failed to parse XML: " + sAXException.getMessage());
        }
    }

    Animation translateDocument(Document paramDocument) throws AnimationParseException {
        translateProcessingInstructions(paramDocument);
        Element element = paramDocument.getDocumentElement();
        if (!element.getTagName().equals("animation"))
            throw new AnimationParseException("invalid document type");
        this._anim = new Animation();
        setAnimationDimensions(element, this._anim);
        NodeList nodeList = element.getChildNodes();
        for (byte b = 0; b < nodeList.getLength(); b++) {
            Node node = nodeList.item(b);
            if (node instanceof Element)
                translateElement((Element) node);
        }
        Animation animation = this._anim;
        this._anim = null;
        return animation;
    }

    void translateProcessingInstructions(Document paramDocument) throws AnimationParseException {
        NodeList nodeList = paramDocument.getChildNodes();
        for (byte b = 0; b < nodeList.getLength(); b++) {
            Node node = nodeList.item(b);
            if (node instanceof ProcessingInstruction)
                translateProcessingInstruction((ProcessingInstruction) node);
        }
    }

    void translateProcessingInstruction(ProcessingInstruction paramProcessingInstruction) throws AnimationParseException {
        if (!paramProcessingInstruction.getTarget().equals("scenebeans"))
            return;
        String str1 = null, str2 = null, str3 = null;
        try {
            PushbackReader pushbackReader = new PushbackReader(new StringReader(paramProcessingInstruction.getData()));
            while (trim(pushbackReader)) {
                String str4 = parseTag(pushbackReader);
                String str5 = parseValue(pushbackReader);
                if (str4.equals("codebase")) {
                    str1 = str5;
                    continue;
                }
                if (str4.equals("category")) {
                    str2 = str5;
                    continue;
                }
                if (str4.equals("package")) {
                    str3 = str5;
                    continue;
                }
                throw new AnimationParseException("unknown element \"" + str4 + "\" in processing instruction");
            }
        } catch (IOException iOException) {
            throw new AnimationParseException("failed to parse processing instruction: " + iOException.getMessage());
        }
        if (str2 == null)
            throw new AnimationParseException("category not specified in processing instruction");
        if (str3 == null)
            throw new AnimationParseException("package not specified in processing instruction");
        if (str1 == null) {
            this._factory.addPackage(str2, str3);
        } else {
            try {
                URLClassLoader uRLClassLoader = new URLClassLoader(new URL[]{new URL(this._doc_url, str1)});
                this._factory.addPackage(str2, uRLClassLoader, str3);
            } catch (MalformedURLException malformedURLException) {
                throw new AnimationParseException("malformed URL in codebase of processing instruction: " + malformedURLException.getMessage());
            }
        }
    }

    private String parseTag(PushbackReader paramPushbackReader) throws AnimationParseException, IOException {
        StringBuffer stringBuffer = new StringBuffer();
        while (true) {
            int i = paramPushbackReader.read();
            if (i == -1)
                throw new AnimationParseException("malformed processing instruction");
            if (i == 61)
                return stringBuffer.toString();
            stringBuffer.append((char) i);
        }
    }

    private String parseValue(PushbackReader paramPushbackReader) throws AnimationParseException, IOException {
        expect(paramPushbackReader, '"');
        StringBuffer stringBuffer = new StringBuffer();
        while (true) {
            int i = paramPushbackReader.read();
            if (i == -1)
                throw new AnimationParseException("malformed processing instruction");
            if (i == 34)
                return stringBuffer.toString();
            stringBuffer.append((char) i);
        }
    }

    private boolean trim(PushbackReader paramPushbackReader) throws IOException {
        int i;
        do {
            i = paramPushbackReader.read();
            if (i == -1)
                return false;
        } while (Character.isWhitespace((char) i));
        paramPushbackReader.unread(i);
        return true;
    }

    private void expect(PushbackReader paramPushbackReader, char paramChar) throws AnimationParseException, IOException {
        int i = paramPushbackReader.read();
        if (i != paramChar)
            throw new AnimationParseException("malformed processing exception");
    }

    private void setAnimationDimensions(Element paramElement, Animation paramAnimation) throws AnimationParseException {
        try {
            String str1 = getOptionalAttribute(paramElement, "width");
            String str2 = getOptionalAttribute(paramElement, "height");
            this._anim.setWidth(ExprUtil.evaluate(str1));
            this._anim.setHeight(ExprUtil.evaluate(str2));
        } catch (NumberFormatException numberFormatException) {
            throw new AnimationParseException("invalid dimension: " + numberFormatException.getMessage());
        }
    }

    void translateElement(Element paramElement) throws AnimationParseException {
        String str = paramElement.getTagName();
        if (str.equals("behaviour")) {
            translateBehaviour(paramElement);
        } else if (str.equals("seq")) {
            translateSeq(paramElement);
        } else if (str.equals("co")) {
            translateCo(paramElement);
        } else if (str.equals("command")) {
            translateCommand(paramElement);
        } else if (str.equals("event")) {
            translateEvent(paramElement);
        } else if (str.equals("define")) {
            translateDefine(paramElement);
        } else if (str.equals("draw")) {
            translateDraw(paramElement);
        } else if (str.equals("forall")) {
            parseForall(paramElement, new ForallParser(this) {
                private final XMLAnimationParser this$0;

                public void parse(Element param1Element) throws AnimationParseException {
                    this.this$0.translateElement(param1Element);
                }
            });
        } else {
            throw new AnimationParseException("invalid element \"" + str + "\"");
        }
    }

    private static interface ForallParser {
        void parse(Element param1Element) throws AnimationParseException;
    }

    void parseForall(Element paramElement, ForallParser paramForallParser) throws AnimationParseException {
        String str1 = getRequiredAttribute(paramElement, "var");
        String str2 = getRequiredAttribute(paramElement, "values");
        String str3 = getOptionalAttribute(paramElement, "sep");
        if (str3 == null)
            str3 = " \n\t";
        StringTokenizer stringTokenizer = new StringTokenizer(str2, str3);
        while (stringTokenizer.hasMoreTokens()) {
            addMacro(str1, stringTokenizer.nextToken());
            NodeList nodeList = paramElement.getChildNodes();
            for (byte b = 0; b < nodeList.getLength(); b++) {
                Node node = nodeList.item(b);
                if (node instanceof Element) {
                    Element element = (Element) node;
                    if (element.getTagName().equals("forall")) {
                        parseForall(element, paramForallParser);
                    } else {
                        paramForallParser.parse(element);
                    }
                }
            }
            removeMacro(str1);
        }
    }

    void translateBehaviour(Element paramElement) throws AnimationParseException {
        Object object = createBehaviour(paramElement);
        if (object instanceof Activity)
            optionalStartActivity(paramElement, (Activity) object);
    }

    void translateSeq(Element paramElement) throws AnimationParseException {
        SequentialActivity sequentialActivity = createSequentialActivity(paramElement);
        optionalStartActivity(paramElement, (Activity) sequentialActivity);
    }

    void translateCo(Element paramElement) throws AnimationParseException {
        ConcurrentActivity concurrentActivity = createConcurrentActivity(paramElement);
        optionalStartActivity(paramElement, (Activity) concurrentActivity);
    }

    SequentialActivity createSequentialActivity(Element paramElement) throws AnimationParseException {
        SequentialActivity sequentialActivity = new SequentialActivity();
        String str = getOptionalAttribute(paramElement, "event");
        if (str != null)
            sequentialActivity.setActivityName(str);
        createSubActivities((CompositeActivity) sequentialActivity, paramElement);
        putOptionalSymbol(paramElement, sequentialActivity);
        return sequentialActivity;
    }

    ConcurrentActivity createConcurrentActivity(Element paramElement) throws AnimationParseException {
        ConcurrentActivity concurrentActivity = new ConcurrentActivity();
        String str = getOptionalAttribute(paramElement, "event");
        if (str != null)
            concurrentActivity.setActivityName(str);
        createSubActivities((CompositeActivity) concurrentActivity, paramElement);
        putOptionalSymbol(paramElement, concurrentActivity);
        return concurrentActivity;
    }

    void createSubActivities(CompositeActivity paramCompositeActivity, Element paramElement) throws AnimationParseException {
        NodeList nodeList = paramElement.getChildNodes();
        for (byte b = 0; b < nodeList.getLength(); b++) {
            Node node = nodeList.item(b);
            if (node instanceof Element)
                createSubActivity(paramCompositeActivity, (Element) node);
        }
    }

    void createSubActivity(CompositeActivity paramCompositeActivity, Element paramElement) throws AnimationParseException {
        String str = paramElement.getTagName();
        if (str.equals("forall")) {
            parseForall(paramElement, new ForallParser(this, paramCompositeActivity) {
                private final CompositeActivity val$ca;
                private final XMLAnimationParser this$0;

                public void parse(Element param1Element) throws AnimationParseException {
                    this.this$0.createSubActivity(this.val$ca, param1Element);
                }
            });
        } else if (str.equals("behaviour")) {
            Object object = createBehaviour(paramElement);
            if (object instanceof Activity && ((Activity) object).isFinite()) {
                paramCompositeActivity.addActivity((Activity) object);
            } else {
                throw new AnimationParseException(paramElement.getTagName() + " elements can only contain finite behaviours");
            }
        } else if (str.equals("co")) {
            paramCompositeActivity.addActivity((Activity) createConcurrentActivity(paramElement));
        } else if (str.equals("seq")) {
            paramCompositeActivity.addActivity((Activity) createSequentialActivity(paramElement));
        } else {
            throw new AnimationParseException("invalid element " + str);
        }
    }

    Object createBehaviour(Element paramElement) throws AnimationParseException {
        try {
            String str1 = getRequiredAttribute(paramElement, "algorithm");
            String str2 = getOptionalAttribute(paramElement, "event");
            Object object = this._factory.newBean("behaviour", str1);
            BeanInfo beanInfo = BeanUtil.getBeanInfo(object);
            if (str2 != null)
                if (object instanceof Activity) {
                    BeanUtil.setProperty(object, beanInfo, "activityName", str2, this._value_parser);
                } else {
                    throw new AnimationParseException("only activities report completion events");
                }
            initialiseParameters(object, beanInfo, paramElement);
            putOptionalSymbol(paramElement, object);
            return object;
        } catch (RuntimeException runtimeException) {
            throw runtimeException;
        } catch (Exception exception) {
            throw new AnimationParseException("could not create behaviour: " + exception.getMessage());
        }
    }

    void optionalStartActivity(Element paramElement, Activity paramActivity) throws AnimationParseException {
        String str = getOptionalAttribute(paramElement, "state");
        if (str != null && str.equals("started"))
            this._anim.addActivity(paramActivity);
    }

    void translateCommand(Element paramElement) throws AnimationParseException {
        String str = getRequiredAttribute(paramElement, "name");
        if (this._anim.getCommand(str) != null)
            throw new AnimationParseException("a command named \"" + str + "\" has already been defined");
        Command command = createCompositeCommand(paramElement);
        this._anim.addCommand(str, command);
    }

    void translateEvent(Element paramElement) throws AnimationParseException {
        String str1 = getRequiredAttribute(paramElement, "object");
        String str2 = getRequiredAttribute(paramElement, "event");
        Object object = getSymbol(str1);
        Command command = createCompositeCommand(paramElement);
        EventInvoker eventInvoker = new EventInvoker(str2, command);
        BeanUtil.bindEventListener(eventInvoker, object);
        this._event_links.add(new EventLink(object, str1, eventInvoker));
    }

    Command createCompositeCommand(Element paramElement) throws AnimationParseException {
        NodeList nodeList = paramElement.getChildNodes();
        CompositeCommand compositeCommand = new CompositeCommand();
        if (nodeList.getLength() == 0)
            throw new AnimationParseException("empty command body");
        for (byte b = 0; b < nodeList.getLength(); b++) {
            Node node = nodeList.item(b);
            if (node instanceof Element) {
                Element element = (Element) node;
                if (element.getTagName().equals("forall")) {
                    parseForall(element, new ForallParser(this, compositeCommand) {
                        private final CompositeCommand val$cmd;
                        private final XMLAnimationParser this$0;

                        public void parse(Element param1Element) throws AnimationParseException {
                            this.val$cmd.addCommand(this.this$0.createSubCommand(param1Element));
                        }
                    });
                } else {
                    compositeCommand.addCommand(createSubCommand(element));
                }
            }
        }
        if (compositeCommand.getCommandCount() == 1)
            return compositeCommand.getCommand(0);
        return (Command) compositeCommand;
    }

    Command createSubCommand(Element paramElement) throws AnimationParseException {
        String str = paramElement.getTagName();
        if (str.equals("start"))
            return createStartCommand(paramElement);
        if (str.equals("stop"))
            return createStopCommand(paramElement);
        if (str.equals("reset"))
            return createResetCommand(paramElement);
        if (str.equals("set"))
            return createSetCommand(paramElement);
        if (str.equals("invoke"))
            return createInvokeCommand(paramElement);
        if (str.equals("announce"))
            return createAnnounceCommand(paramElement);
        throw new AnimationParseException("unexpected element type \"" + str + "\"");
    }

    Command createStartCommand(Element paramElement) throws AnimationParseException {
        String str = getRequiredAttribute(paramElement, "behaviour");
        Object object = getSymbol(str);
        if (object instanceof Activity) {
            Animation animation;
            Activity activity = (Activity) object;
            ActivityRunner activityRunner = activity.getActivityRunner();
            if (activityRunner == null)
                animation = this._anim;
            return (Command) new StartActivityCommand(activity, (ActivityRunner) animation);
        }
        throw new AnimationParseException("symbol \"" + str + "\" does not refer to an activity");
    }

    Command createStopCommand(Element paramElement) throws AnimationParseException {
        String str = getRequiredAttribute(paramElement, "behaviour");
        Object object = getSymbol(str);
        if (object instanceof Activity)
            return (Command) new StopActivityCommand((Activity) object);
        throw new AnimationParseException("symbol \"" + str + "\" does not refer to an activity");
    }

    Command createResetCommand(Element paramElement) throws AnimationParseException {
        String str = getRequiredAttribute(paramElement, "behaviour");
        Object object = getSymbol(str);
        if (object instanceof Activity)
            return (Command) new ResetActivityCommand((Activity) object);
        throw new AnimationParseException("symbol \"" + str + "\" does not refer to an activity");
    }

    Command createSetCommand(Element paramElement) throws AnimationParseException {
        String str1 = getRequiredAttribute(paramElement, "object");
        String str2 = getRequiredAttribute(paramElement, "param");
        String str3 = getRequiredAttribute(paramElement, "value");
        Object object1 = getSymbol(str1);
        BeanInfo beanInfo = BeanUtil.getBeanInfo(object1);
        PropertyDescriptor propertyDescriptor = BeanUtil.getPropertyDescriptor(beanInfo, str2);
        Object object2 = this._value_parser.newObject(propertyDescriptor.getPropertyType(), str3);
        Method method = propertyDescriptor.getWriteMethod();
        return (Command) new SetParameterCommand(object1, method, object2);
    }

    Command createInvokeCommand(Element paramElement) throws AnimationParseException {
        Animation animation;
        String str1 = getRequiredAttribute(paramElement, "command");
        String str2 = getOptionalAttribute(paramElement, "object");
        if (str2 == null) {
            animation = this._anim;
        } else {
            Object object = getSymbol(str2);
            if (object instanceof Animation) {
                animation = (Animation) object;
            } else {
                throw new AnimationParseException("symbol \"" + str2 + "\" does not refer to an animation");
            }
        }
        Command command = animation.getCommand(str1);
        if (command != null)
            return command;
        throw new AnimationParseException("command \"" + str1 + "\" not supported by animation");
    }

    Command createAnnounceCommand(Element paramElement) throws AnimationParseException {
        String str = getRequiredAttribute(paramElement, "event");
        this._anim.addEventName(str);
        return (Command) new AnnounceCommand(this._anim, str);
    }

    void translateDefine(Element paramElement) throws AnimationParseException {
        SceneGraph sceneGraph = createDrawNode(paramElement);
        putOptionalSymbol(paramElement, sceneGraph);
    }

    void translateDraw(Element paramElement) throws AnimationParseException {
        SceneGraph sceneGraph = createDrawNode(paramElement);
        putOptionalSymbol(paramElement, sceneGraph);
        this._anim.addSubgraph(sceneGraph);
    }

    SceneGraph createDrawNode(Element paramElement) throws AnimationParseException {
        return minimise((CompositeNode) createChildren(paramElement));
    }

    SceneGraph createSceneGraph(Element paramElement) throws AnimationParseException {
        String str = paramElement.getTagName();
        SceneGraph sceneGraph = null;
        if (str.equals("draw")) {
            sceneGraph = createDrawNode(paramElement);
        } else if (str.equals("transform")) {
            sceneGraph = createTransformNode(paramElement);
        } else if (str.equals("style")) {
            sceneGraph = createStyleNode(paramElement);
        } else if (str.equals("input")) {
            sceneGraph = createInputNode(paramElement);
        } else if (str.equals("compose")) {
            sceneGraph = createComposeNode(paramElement);
        } else if (str.equals("paste")) {
            sceneGraph = createInstNode(paramElement);
        } else if (str.equals("include")) {
            sceneGraph = createIncludeNode(paramElement);
        } else if (str.equals("primitive")) {
            sceneGraph = createPrimitiveNode(paramElement);
        } else {
            throw new AnimationParseException("unknown scene-graph type \"" + str + "\"");
        }
        return sceneGraph;
    }

    Layered createChildren(Element paramElement) throws AnimationParseException {
        Layered layered = new Layered();
        createChildren((CompositeNode) layered, paramElement);
        return layered;
    }

    CompositeNode createChildren(CompositeNode paramCompositeNode, Element paramElement) throws AnimationParseException {
        NodeList nodeList = paramElement.getChildNodes();
        for (byte b = 0; b < nodeList.getLength(); b++) {
            Node node = nodeList.item(b);
            if (node instanceof Element) {
                Element element = (Element) nodeList.item(b);
                String str = element.getTagName();
                if (str.equals("forall")) {
                    parseForall(element, new ForallParser(this, paramCompositeNode) {
                        private final CompositeNode val$composite;
                        private final XMLAnimationParser this$0;

                        public void parse(Element param1Element) throws AnimationParseException {
                            this.val$composite.addSubgraph(this.this$0.createSceneGraph(param1Element));
                        }
                    });
                } else if (!str.equals("param") && !str.equals("animate")) {
                    paramCompositeNode.addSubgraph(createSceneGraph(element));
                }
            }
        }
        if (paramCompositeNode.getSubgraphCount() == 0)
            throw new AnimationParseException("no layers in composite");
        return paramCompositeNode;
    }

    SceneGraph minimise(CompositeNode paramCompositeNode) throws AnimationParseException {
        if (paramCompositeNode.getSubgraphCount() == 0)
            throw new AnimationParseException("no layers in composite");
        if (paramCompositeNode.getSubgraphCount() == 1)
            return paramCompositeNode.getSubgraph(0);
        return (SceneGraph) paramCompositeNode;
    }

    SceneGraph createTransformNode(Element paramElement) throws AnimationParseException {
        Transform transform;
        String str = getRequiredAttribute(paramElement, "type");
        try {
            transform = (Transform) newSceneBean(str);
        } catch (ClassCastException classCastException) {
            throw new AnimationParseException(str + " is not a transform node");
        }
        putOptionalSymbol(paramElement, transform);
        SceneGraph sceneGraph = minimise((CompositeNode) createChildren(paramElement));
        transform.setTransformedGraph(sceneGraph);
        initialiseParameters(transform, BeanUtil.getBeanInfo(transform), paramElement);
        return (SceneGraph) transform;
    }

    SceneGraph createStyleNode(Element paramElement) throws AnimationParseException {
        Style style;
        String str = getRequiredAttribute(paramElement, "type");
        try {
            style = (Style) newSceneBean(str);
        } catch (ClassCastException classCastException) {
            throw new AnimationParseException(str + " is not a style node");
        }
        putOptionalSymbol(paramElement, style);
        SceneGraph sceneGraph = minimise((CompositeNode) createChildren(paramElement));
        style.setStyledGraph(sceneGraph);
        initialiseParameters(style, BeanUtil.getBeanInfo(style), paramElement);
        return (SceneGraph) style;
    }

    SceneGraph createInputNode(Element paramElement) throws AnimationParseException {
        Input input;
        String str = getRequiredAttribute(paramElement, "type");
        try {
            input = (Input) newSceneBean(str);
        } catch (ClassCastException classCastException) {
            throw new AnimationParseException(str + " is not an input node");
        }
        putOptionalSymbol(paramElement, input);
        SceneGraph sceneGraph = minimise((CompositeNode) createChildren(paramElement));
        input.setSensitiveGraph(sceneGraph);
        initialiseParameters(input, BeanUtil.getBeanInfo(input), paramElement);
        return (SceneGraph) input;
    }

    SceneGraph createComposeNode(Element paramElement) throws AnimationParseException {
        CompositeNode compositeNode;
        String str = getRequiredAttribute(paramElement, "type");
        try {
            compositeNode = (CompositeNode) newSceneBean(str);
        } catch (ClassCastException classCastException) {
            throw new AnimationParseException(str + " is not a composite node");
        }
        putOptionalSymbol(paramElement, compositeNode);
        createChildren(compositeNode, paramElement);
        initialiseParameters(compositeNode, BeanUtil.getBeanInfo(compositeNode), paramElement);
        return (SceneGraph) compositeNode;
    }

    SceneGraph createInstNode(Element paramElement) throws AnimationParseException {
        String str = getRequiredAttribute(paramElement, "object");
        Object object = getSymbol(str);
        if (object instanceof SceneGraph)
            return (SceneGraph) object;
        throw new AnimationParseException("link target \"" + str + "\" does not refer to a " + "scene-graph node");
    }

    SceneGraph createIncludeNode(Element paramElement) throws AnimationParseException {
        String str = getRequiredAttribute(paramElement, "src");
        try {
            URL uRL = new URL(this._doc_url, str);
            XMLAnimationParser xMLAnimationParser = new XMLAnimationParser(uRL, getViewComponent());
            NodeList nodeList = paramElement.getChildNodes();
            for (byte b = 0; b < nodeList.getLength(); b++) {
                Node node = nodeList.item(b);
                if (node instanceof Element) {
                    Element element = (Element) nodeList.item(b);
                    if (!element.getTagName().equals("param"))
                        throw new AnimationParseException("only param tags are allowed in a " + paramElement.getTagName() + " node");
                    String str1 = getRequiredAttribute(element, "name");
                    String str2 = getRequiredAttribute(element, "value");
                    xMLAnimationParser.addMacro(str1, str2);
                }
            }
            Animation animation = xMLAnimationParser.parseAnimation();
            this._anim.addActivity((Activity) animation);
            putOptionalSymbol(paramElement, animation);
            return (SceneGraph) animation;
        } catch (MalformedURLException malformedURLException) {
            throw new AnimationParseException("invalid URL " + str + ": " + malformedURLException.getMessage());
        } catch (IOException iOException) {
            throw new AnimationParseException("failed to include animation " + str + ": " + iOException.getMessage());
        }
    }

    SceneGraph createPrimitiveNode(Element paramElement) throws AnimationParseException {
        String str1 = getRequiredAttribute(paramElement, "type");
        String str2 = getOptionalAttribute(paramElement, "drawn");
        Object object = newSceneBean(str1);
        if (!(object instanceof SceneGraph))
            throw new AnimationParseException("type \"" + str1 + "\" is not a SceneGraph class");
        BeanInfo beanInfo = BeanUtil.getBeanInfo(object);
        initialiseParameters(object, beanInfo, paramElement);
        putOptionalSymbol(paramElement, object);
        return (SceneGraph) object;
    }

    Object newSceneBean(String paramString) throws AnimationParseException {
        try {
            return this._factory.newBean("scene", paramString);
        } catch (Exception exception) {
            throw new AnimationParseException("failed to create scene bean: " + exception.getMessage());
        }
    }

    void initialiseParameters(Object paramObject, Element paramElement) throws AnimationParseException {
        initialiseParameters(paramObject, BeanUtil.getBeanInfo(paramObject), paramElement);
    }

    void initialiseParameters(Object paramObject, BeanInfo paramBeanInfo, Element paramElement) throws AnimationParseException {
        NodeList nodeList = paramElement.getChildNodes();
        for (byte b = 0; b < nodeList.getLength(); b++) {
            Node node = nodeList.item(b);
            if (node instanceof Element)
                initialiseParameter(paramObject, paramBeanInfo, (Element) node);
        }
    }

    void initialiseParameter(Object paramObject, BeanInfo paramBeanInfo, Element paramElement) throws AnimationParseException {
        if (paramElement.getTagName().equals("param")) {
            setParameter(paramObject, paramBeanInfo, paramElement);
        } else if (paramElement.getTagName().equals("animate")) {
            animateParameter(paramObject, paramElement);
        } else if (paramElement.getTagName().equals("forall")) {
            parseForall(paramElement, new ForallParser(this, paramObject, paramBeanInfo) {
                private final Object val$bean;
                private final BeanInfo val$info;
                private final XMLAnimationParser this$0;

                public void parse(Element param1Element) throws AnimationParseException {
                    this.this$0.initialiseParameter(this.val$bean, this.val$info, param1Element);
                }
            });
        }
    }

    void setParameter(Object paramObject, BeanInfo paramBeanInfo, Element paramElement) throws AnimationParseException {
        String str1 = getRequiredAttribute(paramElement, "name");
        String str2 = getOptionalAttribute(paramElement, "index");
        String str3 = getRequiredAttribute(paramElement, "value");
        if (str2 == null) {
            BeanUtil.setProperty(paramObject, paramBeanInfo, str1, str3, this._value_parser);
        } else {
            int i;
            try {
                i = (int) Math.floor(ExprUtil.evaluate(str2));
            } catch (IllegalArgumentException illegalArgumentException) {
                throw new AnimationParseException("invalid property index: " + illegalArgumentException.getMessage());
            }
            BeanUtil.setIndexedProperty(paramObject, paramBeanInfo, str1, i, str3, this._value_parser);
        }
    }

    void animateParameter(Object paramObject, Element paramElement) throws AnimationParseException {
        Object object2, object3;
        String str1 = getRequiredAttribute(paramElement, "param");
        String str2 = getOptionalAttribute(paramElement, "index");
        String str3 = getRequiredAttribute(paramElement, "behaviour");
        String str4 = getOptionalAttribute(paramElement, "facet");
        if (str2 == null) {
            object3 = newBehaviourAdapter(paramObject, str1);
        } else {
            int i;
            try {
                i = Integer.parseInt(str2);
            } catch (NumberFormatException numberFormatException) {
                throw new AnimationParseException("invalid property index: " + numberFormatException.getMessage());
            }
            object3 = newIndexedBehaviourAdapter(paramObject, str1, i);
        }
        Object object1 = getSymbol(str3);
        if (str4 != null) {
            str4 = str4 + "Facet";
            object2 = BeanUtil.getProperty(object1, str4);
        } else {
            object2 = object1;
        }
        BeanUtil.bindEventListener(object3, object2);
        this._behaviour_links.add(new BehaviourLink(object1, str3, object2, str4, paramObject, object3, str1));
    }

    Object newBehaviourAdapter(Object paramObject, String paramString) throws AnimationParseException {
        Class clazz = paramObject.getClass();
        String str = adapterMethodName(paramString);
        try {
            Method method = clazz.getMethod(str, new Class[0]);
            return method.invoke(paramObject, new Object[0]);
        } catch (Exception exception) {
            throw new AnimationParseException("could not create adapter for parameter \"" + paramString + "\": " + exception.getMessage());
        }
    }

    Object newIndexedBehaviourAdapter(Object paramObject, String paramString, int paramInt) throws AnimationParseException {
        Class clazz = paramObject.getClass();
        String str = "new" + Character.toUpperCase(paramString.charAt(0)) + paramString.substring(1) + "Adapter";
        try {
            Method method = clazz.getMethod(str, new Class[]{int.class});
            return method.invoke(paramObject, new Object[]{new Integer(paramInt)});
        } catch (Exception exception) {
            throw new AnimationParseException("could not create adapter for parameter \"" + paramString + "\": " + exception.getMessage());
        }
    }

    String adapterMethodName(String paramString) throws AnimationParseException {
        return "new" + Character.toUpperCase(paramString.charAt(0)) + paramString.substring(1) + "Adapter";
    }

    void putOptionalSymbol(Element paramElement, Object paramObject) throws AnimationParseException {
        String str = getOptionalAttribute(paramElement, "id");
        if (str != null)
            putSymbol(str, paramObject);
    }

    public void putSymbol(String paramString, Object paramObject) throws AnimationParseException {
        if (this._symbol_table.containsKey(paramString))
            throw new AnimationParseException("duplicate definition of symbol \"" + paramString + "\"");
        this._symbol_table.put(paramString, paramObject);
    }

    public Object getSymbol(String paramString) throws AnimationParseException {
        if (this._symbol_table.containsKey(paramString))
            return this._symbol_table.get(paramString);
        throw new AnimationParseException("symbol \"" + paramString + "\" has not been defined");
    }

    public Map getSymbols() {
        return Collections.unmodifiableMap(this._symbol_table);
    }

    public Collection getBehaviourLinks() {
        return Collections.unmodifiableList(this._behaviour_links);
    }

    public Collection getEventLinks() {
        return Collections.unmodifiableList(this._event_links);
    }

    String getRequiredAttribute(Element paramElement, String paramString) throws AnimationParseException {
        try {
            String str = XMLUtil.getRequiredAttribute(paramElement, paramString);
            return this._macro_table.expandMacros(str);
        } catch (MacroException macroException) {
            throw new AnimationParseException(macroException.getMessage());
        }
    }

    String getOptionalAttribute(Element paramElement, String paramString) throws AnimationParseException {
        try {
            String str = XMLUtil.getOptionalAttribute(paramElement, paramString);
            if (str != null)
                str = this._macro_table.expandMacros(str);
            return str;
        } catch (MacroException macroException) {
            throw new AnimationParseException(macroException.getMessage());
        }
    }

    public void addMacro(String paramString1, String paramString2) throws AnimationParseException {
        try {
            this._macro_table.addMacro(paramString1, paramString2);
        } catch (MacroException macroException) {
            throw new AnimationParseException(macroException.getMessage());
        }
    }

    public void removeMacro(String paramString) {
        this._macro_table.removeMacro(paramString);
    }
}
