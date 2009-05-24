/*
 * Copyright (c) 2007-2009, Osmorc Development Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright notice, this list
 *       of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright notice, this
 *       list of conditions and the following disclaimer in the documentation and/or other
 *       materials provided with the distribution.
 *     * Neither the name of 'Osmorc Development Team' nor the names of its contributors may be
 *       used to endorse or promote products derived from this software without specific
 *       prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.osmorc.frameworkintegration;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Robert F. Beeger (robert@beeger.net)
 */
public class FrameworkInstanceUpdateNotifier
{
  public FrameworkInstanceUpdateNotifier()
  {
    _listeners = new ArrayList<Listener>();
  }

  public void fireUpdateFrameworkInstance(FrameworkInstanceDefinition frameworkInstanceDefinition,
                                          UpdateKind updateKind)
  {
    for (Listener listener : _listeners)
    {
      listener.updateFrameworkInstance(frameworkInstanceDefinition, updateKind);
    }
  }

  public void fireUpdateFrameworkInstanceSelection(Project project)
  {
    for (Listener listener : _listeners)
    {
      listener.updateFrameworkInstanceSelection(project);
    }
  }

  public void addListener(@NotNull Listener listener)
  {
    _listeners.add(listener);
  }

  public void removeListener(@NotNull Listener listener)
  {
    _listeners.remove(listener);
  }

  public void fireFrameworkInstanceModuleHandlingChanged(Project project)
  {
    for (Listener listener : _listeners)
    {
      listener.frameworkInstanceModuleHandlingChanged(project);
    }
  }

  private final List<Listener> _listeners;

  public interface Listener
  {
    public void updateFrameworkInstance(@NotNull FrameworkInstanceDefinition frameworkInstanceDefinition,
                                        @NotNull UpdateKind updateKind);

    void updateFrameworkInstanceSelection(@NotNull Project project);

    void frameworkInstanceModuleHandlingChanged(@NotNull Project project);
  }

  public enum UpdateKind
  {
    ADDITION, REMOVAL, RELOAD
  }
}