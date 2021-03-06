/*
 * Copyright 2000-2006 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jetbrains.communicator;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.Presentation;
import jetbrains.communicator.core.*;
import jetbrains.communicator.core.impl.EventBroadcasterImpl;
import jetbrains.communicator.core.impl.transport.CodePointerEventProvider;
import jetbrains.communicator.core.impl.transport.GetProjectsDataProvider;
import jetbrains.communicator.core.impl.transport.GetVFileContentsProvider;
import jetbrains.communicator.core.transport.TextMessageEventProvider;
import jetbrains.communicator.core.transport.XmlResponseProvider;
import jetbrains.communicator.core.users.User;
import jetbrains.communicator.core.users.UserModel;
import jetbrains.communicator.ide.IDEFacade;
import jetbrains.communicator.ide.OwnMessageEvent;
import jetbrains.communicator.p2p.BecomeAvailableXmlMessage;
import jetbrains.communicator.util.StringUtil;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NonNls;
import org.picocontainer.Disposable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Kir
 */
@SuppressWarnings({"ALL"})
public abstract class BaseTestCase extends LightTestCase {
  @NonNls
  private static Logger LOG;

  protected static final DataContext NULL_DATA_CONTEXT = new DataContext() {
    public Object getData(String dataId) {
      return null;
    }
  };
  private IDEtalkListener myListener;
  protected final List<IDEtalkEvent> myEvents = new ArrayList<IDEtalkEvent>();
  protected IDEtalkOptions myOptions;

  protected BaseTestCase() {
    super();
  }

  public BaseTestCase(String s) {
    super(s);
  }
                        
  protected void setUp() throws Exception {
    super.setUp();

    StringUtil.setMyUsername("user_" + getName());
    disposeOnTearDown(new Disposable(){
      public void dispose() {
        StringUtil.setMyUsername(null);
      }
    });

    TestFactory.init();
    myOptions = Pico.getOptions();
  }

  protected void tearDown() throws Exception {
    if (myListener != null) {
      getBroadcaster().removeListener(myListener);
    }

    super.tearDown();

    EventBroadcasterImpl broadcasterImpl = getBroadcasterImpl();
    try {
      if (broadcasterImpl != null) {
        assertEquals("Non-removed listeners found:" +
            Arrays.asList(broadcasterImpl.getListeners()), 0, broadcasterImpl.getListeners().length);
      }
    }
    finally{
      if (broadcasterImpl != null) {
        broadcasterImpl.clearListeners();
      }
      Pico.disposeInstance();
      TestFactory.deleteFiles();
    }
  }


  protected void registerResponseProviders(UserModel userModel, IDEFacade ideFacade) {
    final XmlResponseProvider providers[] = new XmlResponseProvider[]{
        new GetVFileContentsProvider(ideFacade, userModel),
        new GetProjectsDataProvider(ideFacade, userModel),
        new CodePointerEventProvider(getBroadcaster()),
        new TextMessageEventProvider(getBroadcaster()),
        new BecomeAvailableXmlMessage()
    };
    for (int i = 0; i < providers.length; i++) {
      Pico.getInstance().registerComponentInstance(providers[i]);
    }
    disposeOnTearDown(new Disposable(){
      public void dispose() {
        for (int i = 0; i < providers.length; i++) {
          Pico.getInstance().unregisterComponentByInstance(providers[i]);
        }
      }
    });
  }

  public void markLastListenerForCleanup() {
    IDEtalkListener[] listeners = getBroadcasterImpl().getListeners();
    final IDEtalkListener toRemove = listeners[listeners.length - 1];
    disposeOnTearDown(new Disposable() {
      public void dispose() {
        getBroadcaster().removeListener(toRemove);
      }
    });
  }

  private EventBroadcasterImpl getBroadcasterImpl() {
    return ((EventBroadcasterImpl) getBroadcaster());
  }

  public void runBare() throws Throwable {
    try {
      Pico.initInTests();
      LOG = Logger.getLogger(getClass());

      LOG.info("+++++++++++++++++++++++> " + getName());
      setUp();
      assertNotNull(Pico.getEventBroadcaster());
      runTest();
      verify();
    }
    catch(Throwable e) {
      e.printStackTrace();
      throw e;
    }
    finally{
      tearDown();
      LOG.info("-----------------------> " + getName());
    }
  }

  public EventBroadcaster getBroadcaster() {
    return Pico.getEventBroadcaster();
  }

  protected void addEventListener() {
    if (myListener == null) {
      myListener = createListener();
      getBroadcaster().addListener(myListener);
    }
  }

  protected IDEtalkAdapter createListener() {
    return new IDEtalkAdapter() {
      public void afterChange(IDEtalkEvent event) {
        myEvents.add(event);
      }
    };
  }

  protected IDEtalkEvent checkEvent(boolean requireOneEvent) {
    if (requireOneEvent) {
      assertEquals(myEvents.toString(), 1, myEvents.size());
    }
    return (IDEtalkEvent) myEvents.remove(0);
  }

  protected void verifySendMessageLocalEvent(User user, String message) {
    assertEquals(1, myEvents.size());
    assertTrue(myEvents.get(0) instanceof OwnMessageEvent);
    assertEquals(message, ((OwnMessageEvent) myEvents.get(0)).getMessage());
    assertEquals(user, ((OwnMessageEvent) myEvents.get(0)).getTargetUser());
  }

  protected static AnActionEvent createActionEvent(Presentation presentation) throws IllegalAccessException, InvocationTargetException, InstantiationException {
    return createActionEvent(presentation, NULL_DATA_CONTEXT);
  }

  protected static AnActionEvent createActionEvent(Presentation presentation, DataContext dataContext) throws IllegalAccessException, InvocationTargetException, InstantiationException {
    Constructor constructor = AnActionEvent.class.getConstructors()[0];
    if (constructor.getParameterTypes().length == 5) {
      return (AnActionEvent) constructor.newInstance(new Object[]{
                null,
                dataContext,
                "place",
                presentation,
                new Integer(0)
                });
    }
    else {
      return (AnActionEvent) constructor.newInstance(new Object[]{
                null,
                dataContext,
                "place",
                presentation,
                null,
                new Integer(0)
                });
    }
  }
}
