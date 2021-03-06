package UserCreator;


/**
* UserCreator/CreatorHelper.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from UserCreator.idl
* Thursday, May 17, 2018 7:45:14 PM CST
*/

abstract public class CreatorHelper
{
  private static String  _id = "IDL:UserCreator/Creator:1.0";

  public static void insert (org.omg.CORBA.Any a, UserCreator.Creator that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static UserCreator.Creator extract (org.omg.CORBA.Any a)
  {
    return read (a.create_input_stream ());
  }

  private static org.omg.CORBA.TypeCode __typeCode = null;
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      __typeCode = org.omg.CORBA.ORB.init ().create_interface_tc (UserCreator.CreatorHelper.id (), "Creator");
    }
    return __typeCode;
  }

  public static String id ()
  {
    return _id;
  }

  public static UserCreator.Creator read (org.omg.CORBA.portable.InputStream istream)
  {
    return narrow (istream.read_Object (_CreatorStub.class));
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, UserCreator.Creator value)
  {
    ostream.write_Object ((org.omg.CORBA.Object) value);
  }

  public static UserCreator.Creator narrow (org.omg.CORBA.Object obj)
  {
    if (obj == null)
      return null;
    else if (obj instanceof UserCreator.Creator)
      return (UserCreator.Creator)obj;
    else if (!obj._is_a (id ()))
      throw new org.omg.CORBA.BAD_PARAM ();
    else
    {
      org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate ();
      UserCreator._CreatorStub stub = new UserCreator._CreatorStub ();
      stub._set_delegate(delegate);
      return stub;
    }
  }

  public static UserCreator.Creator unchecked_narrow (org.omg.CORBA.Object obj)
  {
    if (obj == null)
      return null;
    else if (obj instanceof UserCreator.Creator)
      return (UserCreator.Creator)obj;
    else
    {
      org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate ();
      UserCreator._CreatorStub stub = new UserCreator._CreatorStub ();
      stub._set_delegate(delegate);
      return stub;
    }
  }

}
