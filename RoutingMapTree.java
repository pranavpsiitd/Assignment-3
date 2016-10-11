import java.util.InputMismatchException;

/**
 * Created by Pranav ps on 18-09-2015.
 */
public class RoutingMapTree {

    protected Exchange root;     //field for storing the root



    public RoutingMapTree(Exchange root){
        this.root=root;
    }  //constructor

    public RoutingMapTree(){
        root = new Exchange(0);
    }      //default constructor

    public Exchange getRoot() {
        return root;
    }               //method to get the root

    public boolean isExternal(Exchange b){
        return b.numChildren()==0;
    }

    public boolean containsNode(Exchange a){      //checks whether 'a' exchange is already present or not
        boolean flag = false;
        if(root.getId()==a.getId()) //Because of uniqueness of the identifier
        {
            flag = true;
        }
        ExchangeNode en = root.children.getHead();
        for(int i=0;i<root.numChildren();i++)
        {

            RoutingMapTree temp = new RoutingMapTree(en.getData());
            flag= flag|(temp.containsNode(a));
            en = en.getNext();
        }
        return flag;
    }

    public void switchOn(MobilePhone a,Exchange b) throws Exception {
            if(!containsNode(b))
                throw new Exception("No such Exchange in the Routing Map Tree");
            if(!isExternal(b)) throw new Exception("Exchange not a base Exchange"); //added extra
            if(root.getResidentSet().IsMember(a)){ //added extra to distinguish
               if(returnMobilePhonebyID(a.number()).status())  //difference form move phone
                   throw new Exception("Phone already on in some other Exchange");
                else {
                   switchOff(a);
                   a.setBaseStation(null);
                   deleteMP(a, returnbs(a));

                   a.switchOn();//added extra
                   a.setBaseStation(b);
                   addMP(a, b);

               }
            }
            else{
                a.switchOn();
                a.setBaseStation(b);
                addMP(a, b);
            }
    }

    public void switchOff(MobilePhone a) throws Exception {
        if(!root.getResidentSet().IsMember(a)){
            throw new Exception("Mobile does not Exist");
        }
        else{
            a.switchOff();
//            a.setBaseStation(null); //removed to distinguish
//            deleteMP(a, returnbs(a));
        }
    }

    public void addMP(MobilePhone a,Exchange b) throws Exception {
        if(!containsNode(b))
            throw new Exception("No such Exchange in the Routing Map Tree");


            Exchange temp = b;
            while(temp.getParent()!=null){
                temp.getResidentSet().Insert(a);
                temp = temp.getParent();
            }
        temp.getResidentSet().Insert(a);

    }

    public void deleteMP(MobilePhone a,Exchange b) throws Exception {
        if(!containsNode(b))
            throw new Exception("No such Exchange in the Routing Map Tree");
        if(!root.getResidentSet().IsMember(a)){
            throw new Exception("No such Mobile Phone");
        }
        else{
            Exchange temp = b;
            while(temp!=null){
                temp.getResidentSet().Delete(a);
                temp = temp.getParent();
            }
        }
    }

    public Exchange returnbs(MobilePhone mp) throws Exception {
        Exchange temporary = null;
        if(isExternal(root)&&(root.getResidentSet().IsMember(mp))){
            temporary = root;
        }
        else{
            for(int i=1;i<=root.numChildren();i++){
                RoutingMapTree temp = new RoutingMapTree(root.child(i));
                temporary = temp.returnbs(mp);
                if(temporary!=null) return temporary ;
            }
        }
        return temporary;
    }

    public boolean containsNodebyID(int id){ //whether a exchange with a given id exists or not
        Exchange a = new Exchange(id);
        return containsNode(a);
    }

    public Exchange returnExchangebyID(int id) throws Exception {
            Exchange temp=null;
        if(root.getId()==id) {
            temp = root;
            return temp;
        }
        else {
            for (int i = 1; i <= root.numChildren(); i++) {
                temp = root.subtree(i).returnExchangebyID(id);
                if(temp!=null) return temp;
            }
        }
        return temp;
    }

    public MobilePhone returnMobilePhonebyID(int a){
        MobilePhone temp = null;
        MobilePhone m = new MobilePhone(a);
        temp = root.getResidentSet().find(m);
        return temp;
    }

    public void addExchange(Exchange a,int b) throws Exception { //b to be added as a child of a
        if(!containsNode(a)){
            throw new Exception("No such node exists");
        }
        else{
            if(containsNodebyID(b))
                throw new Exception("Node to be added already exists");
            else{
                Exchange b_new = new Exchange(b);
                b_new.setParent(a);
                a.getChildren().addLast(b_new);
            }
        }
    }

    public void addExchange(int a,int b) throws Exception {
        if(returnExchangebyID(a)==null){
            throw new Exception("No such Exchange exists");
        }
        addExchange(returnExchangebyID(a),b);
    }

    public Exchange containsMP(int id ) throws Exception {  //returns the 0 Level Exchange containing the mobile phone
        Exchange temp=null;
        MobilePhone mp = new MobilePhone(id);
        if(root.getResidentSet().IsMember(mp)){
           if(isExternal(root)) return root;
            else {
               for(int i=1;i<=root.numChildren();i++){
                   temp = root.subtree(i).containsMP(id);
                   if(temp!=null){
                       return temp;
                   }
               }
           }
        }
        return temp;
    }

    public void switchOnMobile(int a,int b) throws Exception {
        if(!containsNodebyID(b))
            throw new Exception("No such Exchange exists");
        if(containsMP(a)!=null){
            switchOn(returnMobilePhonebyID(a),returnExchangebyID(b));//added extra
//            if(!returnMobilePhonebyID(a).status()) {
////                switchOffMobile(a);    // removed extra to distinguish
//                switchOnMobile(a, b);
//            }
//            else                //added extra
//                throw new Exception("Phone already on in some other Exchange");
        }
        else{
            MobilePhone mpa = new MobilePhone(a);
            switchOn(mpa, returnExchangebyID(b));
        }
    }

    public void switchOffMobile(int a) throws Exception {
        if(containsMP(a)==null) throw new Exception ("Mobile Phone does not exist");
        else{
            switchOff(returnMobilePhonebyID(a));
        }
    }

    public void queryNthChild(int a,int b) throws Exception {
        System.out.println(returnExchangebyID(a).child(b+1).getId());
    }

    public void queryMobilePhoneSet(int a) throws Exception {
        if(returnExchangebyID(a)==null) throw new Exception("No such Exchange");
        Node n = returnExchangebyID(a).getResidentSet().Mysetused().Listused().getHead();
        if(returnExchangebyID(a).getResidentSet().size()==0){
            throw new Exception("No mobile phones registered with this exchange");
        }
        for(int i=1;i<=returnExchangebyID(a).getResidentSet().size();i++){
            MobilePhone m = (MobilePhone) n.getData();
            if(m.status()==true) {  //Added Extra to distinguish
                if (i == 1) System.out.print(m.number());
                else {
                    System.out.print(", " + m.number());
                }
            }
            n = n.getNext();
        }
        System.out.println();
    }


    /*      PART OF ASSIGNMENT 3 STARTS AND CONTINUES UNTIL THE performACTION METHOD

    */

    public Exchange findPhone(MobilePhone m) throws Exception {
        if (containsMP(m.number())==null||(!returnMobilePhonebyID(m.number()).status())){
            throw  new Exception("Mobile Phone does not exist or is switched off");
        }
        else
            return containsMP(m.number());
    }

    public Exchange lowestRouter(Exchange a, Exchange b) throws Exception {
        if((!containsNode(a))||(!containsNode(b)))
            throw new Exception("Exchange a or b does not exist");
        if((!isExternal(a))||(!isExternal(b)))
            throw new  Exception(" a or b is not a base exchange");
        boolean flag = false;
        Exchange temp = a;
        while((!flag)&&(temp!=null)){
            RoutingMapTree temptr = new RoutingMapTree(temp);
            if(temptr.containsNode(b)){
                flag = true;
            }
            else
                temp = temp.getParent();
        }
        return temp;

    }

    public ExchangeList routeCall(MobilePhone a, MobilePhone b) throws Exception {
        if(!root.getResidentSet().IsMember(a)){
            throw new InputMismatchException("Calling Mobile Phone does not exist");
        }
        if(!root.getResidentSet().IsMember(b)){
            throw new IllegalAccessException("Mobile Phone to be called not reachable");
        }
        if(!a.status()){
            throw new IllegalArgumentException("Calling mobile phone is switched Off");
        }
        if(!b.status()){
            throw new IllegalStateException("Mobile Phone to be called is switched off");
        }

        ExchangeList l = new ExchangeList();
        Exchange temp = a.location();
        while(temp!=lowestRouter(a.location(),b.location())){
            l.addLast(temp);
            temp = temp.getParent();
        }
//        l.addLast(temp);
        while(temp!=b.location()){
            l.addLast(temp);
            for(int i=1;i<=temp.getChildren().size();i++){
                RoutingMapTree temptr = new RoutingMapTree(temp.child(i));
                if(temptr.containsNode(b.location()))
                {
//                    l.addLast(temp.child(i));
                    temp = temp.child(i);
                    break; //can cause a bug!!!!!!!!!!!!!!
                }
            }
        }
        l.addLast(temp);
        return l;
    }

    public void CallPhone(MobilePhone a, MobilePhone b) throws Exception{
        if(!root.getResidentSet().IsMember(a)){
            throw new InputMismatchException("Calling Mobile Phone does not exist");
        }
        if(!root.getResidentSet().IsMember(b)){
            throw new IllegalAccessException("Mobile Phone to be called not reachable");
        }
        if(!a.status()){
            throw new IllegalArgumentException("Calling mobile phone is switched Off");
        }
        if(!b.status()){
            throw new IllegalStateException("Mobile Phone to be called is switched off");
        }
        if(a.Oncall){
            throw new Exception("Can make only one call from a mobilephone at a time");
        }
       if((!b.Oncall)) {
           a.Oncall = true;
           Thread threada = new Thread(a, String.valueOf(a.number()));
           threada.start();
           b.Oncall = true;
           Thread threadb = new Thread(b,String.valueOf(b.number()));
           threadb.start();
       }
        else{
           System.out.println("Called phone is busy");
       }
    }

    public void movePhone(MobilePhone a, Exchange b) throws Exception {
        if(!root.getResidentSet().IsMember(a)) throw new IllegalStateException ("Mobile phone does not exist");
        if(!containsNode(b)) throw new IllegalAccessException("Exchange b does not exist");
        if(!isExternal(b)) throw new InputMismatchException("Not a base station");
        if(!a.status())
        {   throw new IllegalArgumentException("Mobile phone off");  }
        else{
            a.switchOff();
            switchOn(a,b);
        }

    }

    //Function for supporting the performAction methods
    public void queryFindPhone(int a) throws Exception {
        System.out.println(findPhone(returnMobilePhonebyID(a)).getId());
    }

    public void queryLowestRouter(int a,int b) throws Exception {
        System.out.println(lowestRouter(returnExchangebyID(a), returnExchangebyID(b)).getId());
    }

    public void queryFindCallPath(int a,int b) throws Exception {
        ExchangeList lp = routeCall(returnMobilePhonebyID(a),returnMobilePhonebyID(b));
        ExchangeNode en = lp.getHead();
        int j=1;
        while(en!=null){
            if(j==1) {
                System.out.print(en.getData().getId());
                j++;
            }
            else{
                System.out.print(", " + en.getData().getId());
            }
           en = en.getNext();
        }
        System.out.println();
    }
    public void CallPhone(int a,int b) throws Exception {
            CallPhone(returnMobilePhonebyID(a),returnMobilePhonebyID(b));
    }
    public void movePhone(int a,int b) throws Exception {
            movePhone(returnMobilePhonebyID(a),returnExchangebyID(b));
    }
        //Assignment 3 ends
    public void performAction(String actionMessage)throws Exception{
        String[] arr = actionMessage.split(" ");
//        int end = actionMessage.indexOf(' ');
//        String sString = actionMessage.substring(0,end);
        switch (arr[0]){
            case "addExchange" : try{addExchange(Integer.parseInt(arr[1]), Integer.parseInt(arr[2]));}
                            catch(Exception e){
                                System.out.print("Possible cases are : ");
                                System.out.println("Either the parent Exchange does not exist or Exchange to be added already exists");
                            System.out.println(e.toString());
                            }
                                    break;
            case "switchOnMobile": try{switchOnMobile(Integer.parseInt(arr[1]), Integer.parseInt(arr[2]));}
            catch(Exception e){
                System.out.print("Possible cases are : ");
                System.out.println("Exchange does not exist or Exchange not a base Exchange or Phone already on in some other Exchange ");
                System.out.println(e.toString());
            }
                                    break;
            case "switchOffMobile": try{switchOffMobile(Integer.parseInt(arr[1]));}
                                    catch(Exception e){
                                        System.out.print("Possible cases are : ");
                System.out.println("You are trying to switch off a mobile phone that does not exist");
                                        System.out.println(e.toString());
            }
                                    break;
            case "queryNthChild": try{System.out.print(actionMessage + ": ");
                                    queryNthChild(Integer.parseInt(arr[1]), Integer.parseInt(arr[2]));}
            catch(Exception e){
                System.out.print("Possible cases are : ");
                System.out.println("Invalid Query for the child");
                System.out.println(e.toString());
            }
                                    break;
            case "queryMobilePhoneSet": System.out.print(actionMessage + ": ");
                                       try{ queryMobilePhoneSet(Integer.parseInt(arr[1]));}
                                       catch(Exception e){
                                           System.out.print("Possible cases are : ");
                                           System.out.println("Either No Mobile Phones Registered with this Exchange or No such exchange exists ");
                                           System.out.println(e.toString());
                                       }
                                        break;
            case "queryFindPhone": System.out.print(actionMessage + ": ");
                try {
                    queryFindPhone(Integer.parseInt(arr[1]));
                } catch (NullPointerException e){
                    System.out.println("No Mobile Phone with Identifier "+Integer.parseInt(arr[1])+" found in the network");
                }
                catch (Exception e) {
                    System.out.print("Possible cases are : ");
                    System.out.println("Mobile Phone does not exist or is switched off");
                    System.out.println(e.toString());
                }
                break;

            case "queryLowestRouter": System.out.print(actionMessage + ": ");
                try {
                    queryLowestRouter(Integer.parseInt(arr[1]), Integer.parseInt(arr[2]));
                } catch (Exception e) {
                    System.out.print("Possible cases are : ");
                    System.out.println("Exchange a or b does not exist OR a or b is not a base exchange");
                    System.out.println(e.toString());
                }
                break;

            case "queryFindCallPath": System.out.print(actionMessage + ": ");  //catch block stack
                try {
                    queryFindCallPath(Integer.parseInt(arr[1]), Integer.parseInt(arr[2]));
                } catch (IllegalAccessException e) {
                    System.out.println("Mobile Phone to be called not reachable");
                }
                catch (InputMismatchException e){
                    System.out.println("Calling Mobile Phone does not exist");
                }
                catch(IllegalStateException e){
                    System.out.println("Mobile Phone to be called is switched off");
//                    System.out.println(e.toString());
                }
                catch(IllegalArgumentException e){
                    System.out.println("Calling mobile phone is switched Off");
                }
                break;

            case "movePhone":
                try {
                    movePhone(Integer.parseInt(arr[1]), Integer.parseInt(arr[2]));
                } catch (IllegalAccessException e) {
                    System.out.println("Exchange b does not exist");
                }
                catch (InputMismatchException e){
                    System.out.println("Not a base station");
                }
                catch(IllegalStateException e){
                    System.out.println("Mobile phone does not exist");
                }
                catch(IllegalArgumentException e){
                    System.out.println("Mobile phone off");
                }
                break;
            case "CallPhone": try {
                CallPhone(Integer.parseInt(arr[1]), Integer.parseInt(arr[2]));
            } catch(Exception e){
                System.out.println(e.toString());
            }
                break;
            default: System.out.println("Invalid Input");
        }

    }
}
