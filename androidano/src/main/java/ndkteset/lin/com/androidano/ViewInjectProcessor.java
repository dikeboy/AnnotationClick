package ndkteset.lin.com.androidano;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;


@AutoService(Processor.class)
@SupportedAnnotationTypes({"ndkteset.lin.com.androidano.BindView","ndkteset.lin.com.androidano.AutoView"})
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class ViewInjectProcessor extends AbstractProcessor {
    public static final String MY_VIEW_PATY = "com.lin.annotationclick";

    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        filer = processingEnv.getFiler();
        System.out.println("init process");
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        try{
            System.out.println("start process1112222");
            createImpView(roundEnvironment);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return true;
    }
    void createImpView(RoundEnvironment roundEnvironment) {
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(AutoView.class);
        List<MyViewInfo> list =new ArrayList<MyViewInfo>();
        MyViewParam myViewParam =new MyViewParam();
        MyViewInfo info;
        for (Element element : elements) {
            Set<ExecutableElement> keys = (Set<ExecutableElement>)element.getAnnotationMirrors().get(0).getElementValues().keySet();
            for(ExecutableElement e: keys){
                AnnotationValue value = element.getAnnotationMirrors().get(0).getElementValues().get(e);
                if(e.getSimpleName().toString().equals("values")){
                    String[] values = value.getValue().toString().split(",");

                    for(int i=0;i<values.length;i++){
                        String mValue = values[i].substring(0,values[i].lastIndexOf("."));
                        info =new MyViewInfo();
                        info.name = mValue.substring(mValue.lastIndexOf(".")+1);
                        info.superClass =mValue;
                        list.add(info);
                    }
                }
                else if(e.getSimpleName().toString().equals("types")){
                    String[] values = value.getValue().toString().split(",");

                    for(int i=0;i<values.length;i++){
                        String mValue = values[i].substring(0,values[i].lastIndexOf("."));
                        if(i==0){
                            myViewParam.contextClass = mValue;
                        }
                        else if(i==1){
                            myViewParam.attributeClass = mValue;
                        }
                    }
                }

            }
            System.out.println("name22="+list);
        }
        if(myViewParam.attributeClass!=null){
            writeMyView(list,myViewParam);
        }

    }

    public void writeMyView( List<MyViewInfo> list,MyViewParam myViewParam){
        try {

            TypeName  contextType =ClassName.bestGuess(myViewParam.contextClass);
            TypeName  attributeType =ClassName.bestGuess(myViewParam.attributeClass);
            for(MyViewInfo mInfo : list){

                MethodSpec.Builder constructor1 = MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(ParameterSpec.builder(contextType, "context").build());
                constructor1.addStatement("super($L)","context");

                MethodSpec.Builder constructor2= MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(ParameterSpec.builder(contextType, "context").build())
                        .addParameter(ParameterSpec.builder(attributeType, "attr").build());
                constructor2.addStatement("super($L,$L)","context","attr");


                ClassName clickClass = ClassName.get("com.lin.annotationclick", "MyClickListener");
                TypeName  clickType =ClassName.bestGuess("android.view.View.OnClickListener");
                MethodSpec.Builder onClickListener= MethodSpec.methodBuilder("setOnClickListener")
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(ParameterSpec.builder(clickType, "l").build());
                onClickListener.addStatement("super.setOnClickListener(new $T(l))",clickClass);



                TypeName  typeName =ClassName.bestGuess(mInfo.superClass);
                TypeSpec typeSpec = TypeSpec.classBuilder("Vova"+mInfo.name)
                        .addModifiers(Modifier.PUBLIC)
                        .superclass(typeName)
                        .addMethod(constructor1.build())
                        .addMethod(constructor2.build())
                        .addMethod(onClickListener.build())
                        .build();

                JavaFile javaFile = JavaFile.builder(MY_VIEW_PATY, typeSpec)
                        .build();
                javaFile.writeTo(filer);
            }



            MethodSpec.Builder myMethed = MethodSpec.methodBuilder("getView")
                    .addModifiers(Modifier.PUBLIC);
            myMethed .addParameter(String.class,"name");
            myMethed.addParameter(ParameterSpec.builder(contextType, "context").build());
            myMethed.addParameter(ParameterSpec.builder(attributeType, "attr").build());

            myMethed.returns(Object.class);
            for(MyViewInfo mInfo : list){
                myMethed.addStatement("if($L.equals(\""+mInfo.name+"\")){ return new "+MY_VIEW_PATY+".Vova"+mInfo.name+"(context,attr)","name");
                myMethed.addStatement("}");
            }

            TypeName  iViewBuilder =ClassName.bestGuess(MY_VIEW_PATY+".IViewBuilder");

            myMethed.addStatement("return null");
            TypeSpec typeSpec = TypeSpec.classBuilder("VovaViewBuilder")
                    .addModifiers(Modifier.PUBLIC)
                    .addSuperinterface(iViewBuilder)
                    .addMethod(myMethed.build())
                    .build();


            JavaFile javaFile = JavaFile.builder(MY_VIEW_PATY, typeSpec)
                    .build();
            javaFile.writeTo(filer);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}