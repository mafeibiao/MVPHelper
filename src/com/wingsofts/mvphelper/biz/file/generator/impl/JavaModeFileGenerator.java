package com.wingsofts.mvphelper.biz.file.generator.impl;

import com.intellij.ide.fileTemplates.JavaTemplateUtil;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiJavaCodeReferenceElement;
import com.intellij.psi.PsiMethod;

/**
 * The file generator works in Contract/Presenter Mode.
 *
 * @author DengChao
 * @since 2017/4/10
 */
@SuppressWarnings("ConstantConditions")
public class JavaModeFileGenerator extends BaseFileGenerator {

    public JavaModeFileGenerator(Project project, PsiDirectory contractDir, PsiDirectory modelDir, PsiDirectory viewDir, PsiDirectory presenterDir, String prefix) {
        super(project, contractDir, modelDir, viewDir, presenterDir, prefix);
    }

    @Override
    public void start() {
        generateContract();
        generateModel();
        generateView();
        generatePresenter();
    }

    private void generateView() {
        generateFile(myViewDir, myPrefix + "View", JavaTemplateUtil.INTERNAL_CLASS_TEMPLATE_NAME, (javaFile, psiClass) -> {
            PsiClass contractClass = myShortNamesCache.getClassesByName("I" + myPrefix + "Contract", myProjectScope)[0];
            PsiClass view = contractClass.findInnerClassByName("IView", false);//don't need to search base
            psiClass.getExtendsList().add(myFactory.createReferenceElementByFQClassName("LAMvpView", myProjectScope));
            psiClass.getImplementsList().add(myFactory.createClassReferenceElement(view));
            psiClass.getModifierList().setModifierProperty("public", true);//force 'public interface myPrefixContract'
        });
    }

    private void generateContract() {
        generateFile(myContractDir, "I" + myPrefix + "Contract", JavaTemplateUtil.INTERNAL_INTERFACE_TEMPLATE_NAME, (javaFile, psiClass) -> {
            PsiClass model = myFactory.createInterface("IModel");
            PsiClass view = myFactory.createInterface("IView");//You have to achieve 'View' yourself.
            PsiClass presenter = myFactory.createInterface("IPresenter");

            model.getModifierList().setModifierProperty("public", false);//Remove modifier
            model.getExtendsList().add(myFactory.createReferenceElementByFQClassName("ILAMvpModel", myProjectScope));
            view.getModifierList().setModifierProperty("public", false);
            view.getExtendsList().add(myFactory.createReferenceElementByFQClassName("ILAMvpView", myProjectScope));
            presenter.getModifierList().setModifierProperty("public", false);
            presenter.getExtendsList().add(myFactory.createReferenceElementByFQClassName("ILAMvpPresenter", myProjectScope));
            psiClass.add(model);
            psiClass.add(view);
            psiClass.add(presenter);
            psiClass.getModifierList().setModifierProperty("public", true);//force 'public interface myPrefixContract'

            FileEditorManager fileEditorManager = FileEditorManager.getInstance(myProject);
            OpenFileDescriptor fileDescriptor = new OpenFileDescriptor(myProject, javaFile.getVirtualFile());
            fileEditorManager.openTextEditor(fileDescriptor, true);//Open the Contract
        });
    }

    private void generatePresenter() {
        generateFile(myPresenterDir, myPrefix + "Presenter", JavaTemplateUtil.INTERNAL_CLASS_TEMPLATE_NAME, (javaFile, psiClass) -> {
            PsiClass contractClass = myShortNamesCache.getClassesByName("I" + myPrefix + "Contract", myProjectScope)[0];
            PsiClass presenter = contractClass.findInnerClassByName("IPresenter", false);//don't need to search base


            psiClass.getExtendsList().add(myFactory.createReferenceElementByFQClassName("LAMvpPresenter", myProjectScope));
            psiClass.getImplementsList().add(myFactory.createClassReferenceElement(presenter));
            psiClass.getImplementsList().add(myFactory.createReferenceElementByFQClassName("MvpEventListener", myProjectScope));
            psiClass.getModifierList().setModifierProperty("public", true);//force 'public interface myPrefixContract'
        });
    }

    private void generateModel() {
        generateFile(myModelDir, myPrefix + "Model", JavaTemplateUtil.INTERNAL_CLASS_TEMPLATE_NAME, (javaFile, psiClass) -> {
            PsiClass contractClass = myShortNamesCache.getClassesByName("I" + myPrefix + "Contract", myProjectScope)[0];
            PsiClass model = contractClass.findInnerClassByName("IModel", false);//don't need to search base
            psiClass.getExtendsList().add(myFactory.createReferenceElementByFQClassName("LAMvpModel", myProjectScope));
            psiClass.getImplementsList().add(myFactory.createClassReferenceElement(model));
            psiClass.getModifierList().setModifierProperty("public", true);//force 'public interface myPrefixContract'
        });
    }

}
