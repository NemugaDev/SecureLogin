/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.PrimeFaces;
import technobrain.model.UserLogon;

/**
 *
 * @author morgan
 */
@ManagedBean(name = "login")
@SessionScoped
@Getter
@Setter
public class UserLogin {

    /**
     * Creates a new instance of UserLogin
     */
    private String current_page = "L";
    public UserLogon user;
    public boolean isLogged_in;
    
    public UserLogon new_user;
    
    List<UserLogon> all_users;
    public UserLogin() {
    }
    
    @PostConstruct
    public void in_it()
    {
        all_users=new ArrayList();
        new_user=new UserLogon();
        user=new UserLogon();
        
        //all_users.add(new UserLogon("demo@yahoo.com", "John Doe", 1, "pass123*"));
        /*all_users.add(new UserLogon("usertwo@gmail.com", "Eric Omar", 0, "pass2"));
        all_users.add(new UserLogon("userthree@gmail.com", "John Doe", 0, "pass3"));*/
    }
    
    public String authenticateUser()
    {
           
            UserLogon current = all_users.stream()
  .filter(u -> user.getEmail().equals(u.getEmail())&&user.getPassword().equals(u.getPassword()))
  .findAny()
  .orElse(null);
             isLogged_in=current!=null;
       
          if(!isLogged_in)
          {
              
              showErrorMessage("Invalid email or password");
              return "";
          }
            return "Home.xhtml?faces-redirect=true";
    }
    private void showErrorMessage(String message)
    {
          FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null));

    }
     private void showSuccessMessage(String message)
    {
          FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, message, null));

    }
       boolean isAlphanumeric(final CharSequence cs) {

        final int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isLetterOrDigit(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    public void addUser()
    {
        //check if another user exists first with same email address
       UserLogon current = all_users.stream()
  .filter(u -> new_user.getEmail().equals(u.getEmail()))
  .findAny()
  .orElse(null);
        if(current!=null)
            {
                showErrorMessage("Email already Exists");
                return;
            }
      
        if(new_user.getPassword().length()<=6)
        {
            showErrorMessage("Password should be atleast six characters");
            return;
        }
        if(isAlphanumeric(new_user.getPassword()))
        {
            showErrorMessage("Password should be alhanumeric");
            return;
        }
        all_users.add(new_user);
        new_user=new UserLogon();
        showSuccessMessage("User Added Successfully");
    }
    
    public void logOut() throws IOException
    {
        String path = getLogoutPath("index.xhtml");
        FacesContext.getCurrentInstance().getExternalContext().redirect(path);
        FacesContext context = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        session.invalidate();
        isLogged_in = false;
    }
      
   public String getLogoutPath(String url) {
        ServletContext servletContext = (ServletContext) FacesContext
                .getCurrentInstance().getExternalContext().getContext();
        return servletContext.getContextPath() + "/" + url + "?faces-redirect=true";
    }
       
      public void swtc(String page) {
       current_page = page;
        PrimeFaces.Ajax currentAjax = PrimeFaces.current().ajax();
        currentAjax.update("main_panel");
      
    }
}
