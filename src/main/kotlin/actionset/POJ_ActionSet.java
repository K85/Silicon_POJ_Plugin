package actionset;

import com.sakurawald.silicon.action.abstracts.*;
import com.sakurawald.silicon.action.actionset.abstracts.ActionSet;
import com.sakurawald.silicon.data.beans.Language;

import java.util.ArrayList;

public class POJ_ActionSet extends ActionSet {

    @Override
    public SubmitAction getSubmitAction() {
        return new POJ_SubmitAction();
    }

    @Override
    public LoginAction getLoginAction() {
        return new POJ_LoginAction();
    }

    @Override
    public StatusAction getStatusAction() {
        return new POJ_StatusAction();
    }

    @Override
    public ProblemsAction getProblemsAction() {
        return new POJ_ProblemsAction();
    }

    @Override
    public ProblemDetailAction getProblemDetailAction() {
        return new POJ_ProblemDetailAction();
    }

    @Override
    public SourceDetailAction getSourceDetailAction() {
        return new POJ_SourceDetailAction();
    }

    @Override
    public CompileDetailAction getCompileDetailAction() {
        return new POJ_CompileDetailAction();
    }

    @Override
    public String getBaseURL() {
        return "http://poj.org/";
    }

    @Override
    public String encodeHTML(String rawHTML) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String decodeHTML(String rawHTML) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getActionSetName() {
        return "Peking OJ";
    }

    @Override
    public ArrayList<Language> getSupportLanguages() {
        ArrayList<Language> languages = new ArrayList<>();
        languages.add(new Language("G++", "0"));
        languages.add(new Language("GCC", "1"));
        languages.add(new Language("Java", "2"));
        languages.add(new Language("Pascal", "3"));
        languages.add(new Language("C++", "4"));
        languages.add(new Language("C", "5"));
        languages.add(new Language("Fortran", "6"));
        return languages;
    }

}
