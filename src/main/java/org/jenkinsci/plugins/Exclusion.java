package org.jenkinsci.plugins;

import hudson.Extension;
import hudson.ExtensionPoint;
import hudson.Util;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.util.FormValidation;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

/**
 * @author <a href="mailto:nicolas.deloof@gmail.com">Nicolas De Loof</a>
 */
public class Exclusion extends AbstractDescribableImpl<Exclusion> {

    private final String fqcn;
    private String context;

    @DataBoundConstructor
    public Exclusion(String fqcn, String context) {
        this.fqcn = fqcn;
        this.context = Util.fixEmpty(context);
    }

    public String getFqcn() {
        return fqcn;
    }

    public String getContext() {
        return context;
    }

    @Extension
    public static final class DescriptorImpl extends Descriptor<Exclusion> {

        @Override
        public String getDisplayName() {
            return "Extension";
        }
        
        public FormValidation doCheckFqcn(@QueryParameter String fqcn) {
            Class definedClass;
            try {
                definedClass = Class.forName(fqcn);
            } catch (ClassNotFoundException e) {
                return FormValidation.error(Messages.Exclusion_classNotFoundMsg());
            }

            final boolean isExtension = ExtensionPoint.class.isAssignableFrom(definedClass);
            final boolean isDescriptor = Descriptor.class.isAssignableFrom(definedClass);

            // Handle possible combinations
            if (isExtension && isDescriptor) {
                return FormValidation.ok(Messages.Exclusion_classIsExtensionAndDescriptor());
            }
            if (isExtension) {
                return FormValidation.ok(Messages.Exclusion_classIsExtension());
            }
            if (isDescriptor) {
                return FormValidation.ok(Messages.Exclusion_classIsDescriptor());
            }
            return FormValidation.error(Messages.Exclusion_unsupportedType());
        }
    }
}
